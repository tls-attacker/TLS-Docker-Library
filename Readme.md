# TLS-Docker-Library

## Building
### Inspect container content
```bash
docker build --squash -t <image name> . 
#without squash there are multiple layer.tar files
docker image save <image name> | tar x --wildcards -O "*/layer.tar" | tar t
```
### Get Shell in intermediary container to get runtime dependencies
```bash
docker run --rm -it --cap-add SYS_PTRACE <intermediary image name> /bin/sh 
## ls, ldd, strace, ...
```
### With build arguments
```bash
docker build --build-arg VERSION=0.5 -t bearssl .
```

## Execution
### With certificate volume
```bash
docker run --rm -it -v cert-data:/cert/:ro,nocopy -p 127.0.0.42:<port on host>:<port of internal tls server> <image name> options...
```
### With certificate directory
```bash
docker run --rm -it -v /path/to/dir/:/cert/:ro,nocopy -p 127.0.0.42:<port on host>:<port of internal tls server> <image name> options...
```
### On host network stack
```bash
docker run --rm -it -v cert-data:/cert/:ro,nocopy --network=host <image name> options...
```

## Cleaning / Removing unused images
### Dangling images (layers that have no relationship to any tagged images)
```bash
docker images -f dangling=true
```
### Images with none name
```bash
docker rmi -f $(docker images  | grep none)
```
### Images of sizes 100-999 MB
```bash
docker rmi -f $(docker images | grep -P "\d{3}MB")
```
