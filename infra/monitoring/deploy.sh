#!/bin/bash

NAMESPACE="gabom-monitoring"

helm upgrade --install monitoring prometheus-community/kube-prometheus-stack \
  -n $NAMESPACE \
  -f infra/monitoring/values.yml