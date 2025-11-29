#!/bin/sh

echo ">>> Applying ConfigMap..."
kubectl apply -f config/configmap.yaml

echo ">>> Applying Secret..."
kubectl apply -f config/secret.yaml

echo ">>> Deploying PostgreSQL..."
kubectl apply -f postgres/

echo ">>> Deploying ActiveMQ..."
kubectl apply -f activemq/

echo ">>> Deploying Servlets App..."
kubectl apply -f servlets-app/

echo ">>> Waiting for pods..."
kubectl get pods -o wide
