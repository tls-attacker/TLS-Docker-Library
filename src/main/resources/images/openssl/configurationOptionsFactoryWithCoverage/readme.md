This directory contains factory Dockerfiles to build OpenSSL images with configuration options. Also the code-coverage of the built library is measured during the run of the OpenSSL server/client using gcov and lcov. Since the OpenSSL sources are required for coverage measurement and lcov needs several libraries to work, the final minimal image is much larger in size than the non-coverage version.  

To create a build with configuration options multiple steps has to be done (due to the usage of ccache):

1) The factory image of the respective version has to be built. This only needs to be applied once per version. The argument OpenSSL_1_1_1 has to be a valid branch/tag of OpenSSL's GitHub.

    `docker build -t openssl-cov-fact:1_1_1 --build-arg OPENSSL_BRANCH=OpenSSL_1_1_1 -f ./Dockerfile_Factory_OpenSSL .`

2) The factory (a docker image) can then be run (as a docker container) using the respective configuration options. 
   This starts the build process of OpenSSL. A ccache volume has to be mounted to profit from its faster build time when building multiple builds with different configuration options. The volume has to be mounted at '/src/ccache'.

    `docker run --mount source=ccache-cache,target=/src/ccache --name factory_container openssl-cov-fact:1_1_1 <config options>`

    <config options> can be for example: `no_psk no_ec --with-rand-seed=os,getrandom`

3) When the container is finished it must be committed to a temporary image. This temporary file already contains the built OpenSSL version.

    `docker commit factory_container openssl-cov-temp:example`

    [Optional]

    To inspect the contents of the commited container and/or experiment manually with the image we can
    run start a bash using (with mounted certificate and coverage volume)

    `docker run -p 4433:4433 -p 8090:8090 -it --mount source=coverage,target=/covVolume -v cert-data:/cert/:ro,nocopy --entrypoint bash openssl-cov-temp:example`

4) A final version is created using the Dockerfile_Min_OpenSSL. 
   This image extracts the necessary files from the temporary image to create a lightweight minimal image. The temporary image can be removed afterwards.

    `docker build -t openssl-cov:example --build-arg TEMP_REPRO=openssl-cov-temp\:example -f ./Dockerfile_Min_OpenSSL .`

5) Now, the final image can be run anytime as a docker container. 

   The following command will create a container that stars a TLS server. The OpenSSL server runs on container port 4433 and is accessible at hostport 4434 (can be chosen). To shutdown the server you need to trigger the http service running on container port 8090. If shutdowned by stopping the docker container (e.g. CTRL+C), no coverage data is collected.

    To start a server:

    `docker run -p 4434:4433 -p 8091:8090 -it --mount source=coverage,target=/covVolume -v cert-data:/cert/:ro,nocopy openssl-cov:example server`
    
    To stop the server and collect coverage data:

    `curl http://localhost:8091/shutdown`

    A TLS client container can be started as follows. We need to pass the server address we want to connect to. Note that we cannot use localhost adresses, since this only adresses the docker container itself and not the hostmachine. For a server on 192.168.0.42:4433 we use:

    `docker run -p 8091:8090 -it --mount source=coverage,target=/covVolume -v cert-data:/cert/:ro,nocopy openssl-cov:example client 192.168.0.42:4433`

    To trigger a client connection we can use:

    `curl http://localhost:8091/trigger`

    To shutdown the container and collect coverage data we call:

    `curl http://localhost:8091/shutdown`

    In these cases coverage data is stored in a docker volume named 'coverage'.

