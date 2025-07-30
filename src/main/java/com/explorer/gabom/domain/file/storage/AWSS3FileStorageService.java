package com.explorer.gabom.domain.file.storage;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.explorer.gabom.domain.file.provider.aws.AWSS3ClientProviderForAttachment;
import com.explorer.gabom.domain.file.util.ResourceHashUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AWSS3FileStorageService implements FileStorageService {
	private final AWSS3ClientProviderForAttachment awsS3ClientProviderForAttachment;

	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucket = "";

	@Value("${spring.cloud.aws.s3.key.prefix}")
	private String keyPrefix = "";

	/**
	 * 파일을 S3에 업로드하고 해당 파일의 해시 값을 반환합니다.
	 *
	 * @param file 업로드할 MultipartFile
	 * @return 파일 내용 기반의 SHA-256 해시
	 * @throws IOException 파일 읽기/업로드 실패 시 발생
	 */
	@Override
	public String uploadFile(MultipartFile file) throws IOException {
		String hash = ResourceHashUtil.generateHash(file.getInputStream());
		uploadToS3(file, hash, false);
		return hash;
	}

	/**
	 * 이미지를 S3에 업로드하고 공개 접근 가능한 URL을 반환합니다.
	 *
	 * @param file 업로드할 이미지 파일
	 * @return S3에 저장된 이미지의 URL
	 * @throws IOException 파일 읽기/업로드 실패 시 발생
	 */
	@Override
	public String uploadImage(MultipartFile file) throws IOException {
		String hash = ResourceHashUtil.generateHash(file.getInputStream());

		log.debug("hash = {}", hash);
		log.debug("targetPath = {}", getTargetPath(hash));
		log.debug("keyPrefix = {}", keyPrefix);
		uploadToS3(file, hash, true);
		return hash;
	}

	/**
	 * 파일을 S3에 업로드합니다. 필요에 따라 공개 설정이 가능합니다.
	 *
	 * @param file     업로드할 파일
	 * @param hash     파일 해시 (경로 구성에 사용)
	 * @param isPublic true일 경우 공개 접근(PublicRead) 설정
	 * @throws IOException 업로드 중 오류 발생 시
	 */
	private void uploadToS3(MultipartFile file, String hash, boolean isPublic) throws IOException {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(file.getContentType());
		metadata.setContentLength(file.getSize());

		PutObjectRequest request = new PutObjectRequest(bucket, getTargetPath(hash), file.getInputStream(), metadata);

		// 공개 파일 일경우 PublicRead 설정(이미지)
		if (isPublic) {
			request.withCannedAcl(CannedAccessControlList.PublicRead);
		}

		awsS3ClientProviderForAttachment.getS3Client().putObject(request);
	}

	/**
	 * S3에 저장된 파일을 바이트 배열(byte[])로 가져옵니다.
	 *
	 * <p>전체 파일 내용을 한 번에 메모리로 읽어야 할 때 사용합니다.</p>
	 * <p>예: 이미지 파일을 byte[]로 읽어서 처리하거나, 작은 크기의 첨부파일 다운로드 응답을 구성할 때 적합합니다.</p>
	 *
	 * <pre>{@code
	 * byte[] fileData = fileStorageService.getBytes("abc123");
	 * Files.write(Paths.get("local-file.jpg"), fileData);
	 * }</pre>
	 *
	 * @param hash 저장된 파일의 해시값
	 * @return 파일의 내용이 담긴 byte 배열
	 * @throws IOException 읽기 실패 시 예외 발생
	 */
	@Override
	public byte[] getBytes(String hash) throws IOException {
		S3Object object = awsS3ClientProviderForAttachment.getS3Client().getObject(
			new GetObjectRequest(bucket, this.getTargetPath(hash)));
		S3ObjectInputStream objectInputStream = object.getObjectContent();
		return IOUtils.toByteArray(objectInputStream);
	}

	/**
	 * S3에 저장된 파일의 {@link InputStream}을 반환합니다.
	 *
	 * <p>파일 크기가 크거나, 직접 스트림으로 클라이언트에게 응답할 때 사용합니다.</p>
	 * <p>예: 파일 다운로드 API에서 {@code StreamUtils.copy(inputStream, response.getOutputStream())} 와 같이 사용합니다.</p>
	 *
	 * <pre>{@code
	 * try (InputStream in = fileStorageService.getInputStream("abc123")) {
	 *     StreamUtils.copy(in, response.getOutputStream());
	 * }
	 * }</pre>
	 *
	 * @param hash 저장된 파일의 해시값
	 * @return S3에 저장된 파일의 입력 스트림
	 */
	@Override
	public InputStream getInputStream(String hash) {
		S3Object object = awsS3ClientProviderForAttachment.getS3Client().getObject(
			new GetObjectRequest(bucket, this.getTargetPath(hash)));
		return object.getObjectContent();
	}

	/**
	 * S3에 저장된 파일을 삭제합니다.
	 *
	 * @param hash 삭제할 파일의 해시값
	 */
	@Override
	public void deleteFile(String hash) {
		this.awsS3ClientProviderForAttachment.getS3Client().deleteObject(
			new DeleteObjectRequest(bucket, this.getTargetPath(hash)));
	}

	/**
	 * S3 경로를 구성하는 함수입니다. 해시 값을 디렉토리 및 파일명으로 분리해 반환합니다.
	 *
	 * @param hash 파일의 해시값
	 * @return S3에 저장될 파일 경로
	 */
	@Override
	public String getTargetPath(String hash) {
		String dir = ResourceHashUtil.getDirPath(hash);
		String file = ResourceHashUtil.getFilePath(hash);
		return keyPrefix + dir + "/" + file;
	}

}
