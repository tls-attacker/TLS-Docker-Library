## BOUNCYCASTLE
### Versions
	1.50, 1.51, 1.52, 1.53, 1.54, 1.55, 1.56, 1.57, 1.58
- server successfully tested: all versions
- clients successfully tested: clients not available for this implementation

### Build
- all versions: `python3 build.py -l bouncycastle`
- latest version: `python3 build.py -l bouncycastle:latest`
- specific version: `python3 build.py -l bouncycastle:VERSION`

### Run
- tls server:
  - `docker run -it --rm bouncycastle-server:VERSION`
  - `docker run -v cert-data:/cert/:ro,nocopy -it --rm bouncycastle-server:VERSION 4433 /cert/keys.jks password rsa2048 /cert/keys.jks password ec256`
