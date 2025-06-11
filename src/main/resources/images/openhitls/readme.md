## open-HiTls

### Versions

        0.2.0
        0.2.1

- tested server versions: all
- tested client versions: all

### Build

- all versions: `python3 build.py -l openhitls`
- latest version: `python3 build.py -l openhitls:latest`
- specific version: `python3 build.py -l openhitls:VERSION`

### Run

- run server
  - `docker run openhitls-server:VERSION 8443`
- run client
  - `docker run openhitls-client:VERSION localhost 8443`

