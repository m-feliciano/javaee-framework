#!/bin/shecho ">>> Deleting Servlets App..."
kubectl delete -f servlets-app/ --ignore-not-found=trueecho ">>> Deleting ActiveMQ..."
kubectl delete -f activemq/ --ignore-not-found=trueecho ">>> Deleting PostgreSQL..."
kubectl delete -f postgres --ignore-not-found=true