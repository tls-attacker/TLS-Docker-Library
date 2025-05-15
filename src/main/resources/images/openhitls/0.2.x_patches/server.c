#include <stdio.h>
#include <stdlib.h>

#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include "hitls_build.h"
#include "securec.h"
#include <signal.h>
#include <netinet/tcp.h>

#include "bsl_sal.h"
#include "bsl_err.h"
#include "crypt_algid.h"
#include "crypt_eal_init.h"
#include "crypt_eal_rand.h"
#include "crypt_eal_pkey.h"
#include "crypt_eal_encode.h"
#include "hitls_error.h"
#include "hitls_config.h"
#include "hitls.h"
#include "hitls_cert_init.h"
#include "hitls_cert.h"
#include "hitls_crypt_init.h"
#include "hitls_pki_cert.h"
#include "crypt_errno.h"

#define CERTS_PATH      "/certs/"
#define HTTP_BUF_MAXLEN (18 * 1024) /* 18KB */

/* Helper to immediately close a socket without TIME_WAIT */
void close_socket_immediately(int sockfd)
{
    if (sockfd < 0) {
        return;
    }

    struct linger sl;
    sl.l_onoff = 1;  // Enable linger option
    sl.l_linger = 0; // Timeout of 0 seconds -> immediate reset (RST)

    if (setsockopt(sockfd, SOL_SOCKET, SO_LINGER, &sl, sizeof(sl)) < 0) {
        perror("setsockopt SO_LINGER failed");
    }

    close(sockfd);
}

int main(int32_t argc, char *argv[])
{
    signal(SIGPIPE, SIG_IGN);
    int32_t ret = 0;
    int32_t port = 12345; // Default port

    if (argc >= 2) {
        port = atoi(argv[1]);
        if (port <= 0 || port > 65535) {
            fprintf(stderr, "Invalid port number: %s\n", argv[1]);
            return -1;
        }
    }

    printf("Starting server on port %d...\n", port);
    HITLS_Config *config = NULL;
    HITLS_X509_Cert *rootCA = NULL;
    HITLS_X509_Cert *subCA = NULL;
    HITLS_X509_Cert *serverCert = NULL;
    CRYPT_EAL_PkeyCtx *pkey = NULL;
    int fd = 0;
    int option = 1;

    /* 注册BSL内存能力、仅供参考 */
    BSL_SAL_CallBack_Ctrl(BSL_SAL_MEM_MALLOC, malloc);
    BSL_SAL_CallBack_Ctrl(BSL_SAL_MEM_FREE, free);
    BSL_ERR_Init();

    ret = CRYPT_EAL_Init(CRYPT_EAL_INIT_CPU | CRYPT_EAL_INIT_PROVIDER);
    if (ret != CRYPT_SUCCESS) {
        printf("CRYPT_EAL_Init: error code is %x\n", ret);
        return -1;
    }

    ret = CRYPT_EAL_ProviderRandInitCtx(NULL, CRYPT_RAND_SHA256, "provider=default", NULL, 0, NULL);

    if (ret != CRYPT_SUCCESS) {
        printf("Init rand failed.\n");
        return -1;
    }

    HITLS_CertMethodInit();
    HITLS_CryptMethodInit();

    /* Load TLS config and certificates once */
    config = HITLS_CFG_NewTLSConfig();
    if (config == NULL) {
        printf("HITLS_CFG_NewTLSConfig failed.\n");
        return -1;
    }
    ret = HITLS_CFG_SetClientVerifySupport(config, false);  // disable peer verify
    if (ret != HITLS_SUCCESS) {
        printf("Disable peer verify failed.\n");
        return -1;
    }

    /* Load certificates once */
    ret = HITLS_X509_CertParseFile(BSL_FORMAT_ASN1, CERTS_PATH "ca.der", &rootCA);
    if (ret != HITLS_SUCCESS) {
        printf("Parse ca failed.\n");
        return -1;
    }
    ret = HITLS_X509_CertParseFile(BSL_FORMAT_ASN1, CERTS_PATH "inter.der", &subCA);
    if (ret != HITLS_SUCCESS) {
        printf("Parse subca failed.\n");
        return -1;
    }
    HITLS_CFG_AddCertToStore(config, rootCA, TLS_CERT_STORE_TYPE_DEFAULT, true);
    HITLS_CFG_AddCertToStore(config, subCA, TLS_CERT_STORE_TYPE_DEFAULT, true);
    HITLS_CFG_LoadCertFile(config, CERTS_PATH "server.der", TLS_PARSE_FORMAT_ASN1);
    HITLS_CFG_LoadKeyFile(config, CERTS_PATH "server.key.der", TLS_PARSE_FORMAT_ASN1);

    /* Create socket */
    fd = socket(AF_INET, SOCK_STREAM, 0);
    if (fd == -1) {
        printf("Create socket failed.\n");
        return -1;
    }
    if (setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, &option, sizeof(option)) < 0) {
        printf("setsockopt SO_REUSEADDR failed.\n");
        close(fd);
        return -1;
    }

    struct sockaddr_in serverAddr;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(port);
    serverAddr.sin_addr.s_addr = htonl(INADDR_ANY);
    if (bind(fd, (struct sockaddr *)&serverAddr, sizeof(serverAddr)) != 0) {
        printf("bind failed.\n");
        close(fd);
        return -1;
    }
    if (listen(fd, 5) != 0) {
        printf("listen socket failed\n");
        close(fd);
        return -1;
    }

    printf("Server is listening on port %s...\n", argv[1]);

    /* Main accept loop */
    while (1) {
        int infd = -1;
        HITLS_Ctx *ctx = NULL;
        BSL_UIO *uio = NULL;

        struct sockaddr_in clientAddr;
        unsigned int len = sizeof(clientAddr);
        infd = accept(fd, (struct sockaddr *)&clientAddr, &len);
        if (infd < 0) {
            printf("accept failed.\n");
            continue; // accept next connection
        }

        printf("Accepted connection from %s:%d\n", inet_ntoa(clientAddr.sin_addr), ntohs(clientAddr.sin_port));

        /* Setup HiTLS context per connection */
        ctx = HITLS_New(config);
        if (ctx == NULL) {
            printf("HITLS_New failed.\n");
            close_socket_immediately(infd);
            continue;
        }

        uio = BSL_UIO_New(BSL_UIO_TcpMethod());
        if (uio == NULL) {
            printf("BSL_UIO_New failed.\n");
            HITLS_Free(ctx);
            close_socket_immediately(infd);
            continue;
        }

        ret = BSL_UIO_Ctrl(uio, BSL_UIO_SET_FD, (int32_t)sizeof(fd), &infd);
        if (ret != HITLS_SUCCESS) {
            printf("BSL_UIO_SET_FD failed.\n");
            BSL_UIO_Free(uio);
            HITLS_Free(ctx);
            close_socket_immediately(infd);
            continue;
        }

        ret = HITLS_SetUio(ctx, uio);
        if (ret != HITLS_SUCCESS) {
            printf("HITLS_SetUio failed.\n");
            BSL_UIO_Free(uio);
            HITLS_Free(ctx);
            close_socket_immediately(infd);
            continue;
        }

        ret = HITLS_Accept(ctx);
        if (ret != HITLS_SUCCESS) {
            printf("HITLS_Accept failed.\n");
            HITLS_Close(ctx);
            HITLS_Free(ctx);
            BSL_UIO_Free(uio);
            close_socket_immediately(infd);
            continue;
        }

        /* Communication */
        uint8_t readBuf[HTTP_BUF_MAXLEN + 1] = {0};
        uint32_t readLen = 0;
        ret = HITLS_Read(ctx, readBuf, HTTP_BUF_MAXLEN, &readLen);
        if (ret == HITLS_SUCCESS) {
            printf("Received (%u bytes): %s\n", readLen, readBuf);

            /* Echo response */
            const uint8_t sndBuf[] = "Hi, this is server\n";
            uint32_t writeLen = 0;
            ret = HITLS_Write(ctx, sndBuf, sizeof(sndBuf), &writeLen);
            if (ret != HITLS_SUCCESS) {
                printf("HITLS_Write error: %d\n", ret);
            }
        } else {
            printf("HITLS_Read failed: %d\n", ret);
        }

        /* Cleanup connection */
        HITLS_Close(ctx);
        HITLS_Free(ctx);
        BSL_UIO_Free(uio);
        close_socket_immediately(infd);
        printf("Connection closed.\n");
    }

    /* Cleanup (never reached unless you break the loop) */
    close(fd);
    HITLS_CFG_FreeConfig(config);
    HITLS_X509_CertFree(rootCA);
    HITLS_X509_CertFree(subCA);
    HITLS_X509_CertFree(serverCert);
    CRYPT_EAL_PkeyFreeCtx(pkey);
    return 0;
}
