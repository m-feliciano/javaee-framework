#!/bin/sh

echo ">>> Deleting Servlets App..."
kubectl delete -f servlets-app/ --ignore-not-found=true

echo ">>> Deleting ActiveMQ..."
kubectl delete -f activemq/ --ignore-not-found=true

echo ">>> Deleting PostgreSQL..."
kubectl delete -f postgres --ignore-not-found=true