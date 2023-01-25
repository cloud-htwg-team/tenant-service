docker build . -t tenant-service
docker run -p 8888:8888 --name tenant-service tenant-service
