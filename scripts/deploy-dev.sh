#!/bin/bash

IS_GREEN_EXIST=$(docker ps | grep green)
IS_REDIS_EXIST=$(docker ps | grep redis)

if [ -z "$IS_REDIS_EXIST" ];then
  echo "### REDIS ###"
  echo ">>> pull redis image"
  docker compose pull redis-dev
  echo ">>> up redis container"
  docker compose up -d redis-dev
fi

# green up
if [ -z "$IS_GREEN_EXIST" ];then
  echo "### BLUE -> GREEN ####"
  echo ">>> pull green image"
  docker pull jinkonu/feelin-dev:latest
  echo ">>> remove old green container"

  docker compose -f /home/ubuntu/docker-compose-dev.yml rm -fs green
  echo ">>> up green container"
  docker compose -f /home/ubuntu/docker-compose-dev.yml up -d green

  while [ 1 = 1 ]; do
    echo ">>> green health check ..."
    sleep 3
    REQUEST=$(curl http://127.0.0.1:8084/actuator/health)
    if [ -n "$REQUEST" ]; then
      echo ">>> health check success !"
      break;
    fi
  done;
  sleep 3
  echo ">>> reload nginx"
  sudo cp /etc/nginx/conf.d/green-url.inc /etc/nginx/conf.d/service-url.inc
  sudo nginx -s reload
  echo ">>> down blue container"

  docker compose -f /home/ubuntu/docker-compose-dev.yml stop blue


# blue up
else
  echo "### GREEN -> BLUE ###"
  echo ">>> pull blue image"
  docker pull jinkonu/feelin-dev:latest
  echo ">>> remove old blue container"

  docker compose -f /home/ubuntu/docker-compose-dev.yml rm -fs blue
  echo ">>> up blue container"
  docker compose -f /home/ubuntu/docker-compose-dev.yml up -d blue

  while [ 1 = 1 ]; do
    echo ">>> blue health check ..."
    sleep 3
    REQUEST=$(curl http://127.0.0.1:8083/actuator/health)
    if [ -n "$REQUEST" ]; then
      echo ">>> health check success !"
      break;
    fi
  done;
  sleep 3
  echo ">>> reload nginx"
  sudo cp /etc/nginx/conf.d/blue-url.inc /etc/nginx/conf.d/service-url.inc
  sudo nginx -s reload
  echo ">>> down green container"

  docker compose -f /home/ubuntu/docker-compose-dev.yml stop green

  docker image prune -f
fi