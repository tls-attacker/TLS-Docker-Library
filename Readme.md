# TLS-Docker-Library

## Build Docker images
### First steps
1. Setup the environment
    ```bash
    ./setup.sh
    ```
1. Build the docker images
    * To build every available docker image, or every docker image of a specific TLS Libraries, use the `build.py` script (requires `python >=3.7`)
        ```bash
        # use --help to see the available options
        cd images
        python3 build.py
        ```
    * To build only specific TLS Libraries, use the `-l` parameter of the build script. E.g.: `python3 build.py -l matrixssl`
    * You can specify multiple versions this way and also restrict the building to certain versions with a regex f.e.
        
        ```bash
        cd images
        python3 build.py -l matrixssl:3.* -l bearssl:0.6
        ```

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

## Usage
### Get the CONTAINER ID
```bash
docker ps
```
### Get the IP-Address
```bash
docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' <CONTAINER ID>
```
### Connect directly
```bash
openssl s_client -connect <ip>:<port>
```
### Stop container
```bash
docke container kill <CONTAINER ID>
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