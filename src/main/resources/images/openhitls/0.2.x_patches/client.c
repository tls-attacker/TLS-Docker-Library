#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>

#include "securec.h"

#include "bsl_sal.h"
#include "bsl_err.h"
#include "crypt_eal_init.h"
#include "crypt_algid.h"
#include "crypt_eal_rand.h"
#include "hitls_error.h"
#include "hitls_config.h"
#include "hitls.h"
#include "hitls_cert_init.h"
#include "hitls_cert.h"
#include "hitls_crypt_init.h"
#include "hitls_pki_cert.h"
#include "crypt_errno.h"

#define HTTP_BUF_MAXLEN (18 * 1024) /* 18KB */

int main(int argc, char *argv[])
{
    if (argc != 3) {
        printf("Usage: %s <host> <port>\n", argv[0]);
        return -1;
    }

    const char *host = argv[1];
    int port = atoi(argv[2]);
    if (port <= 0 || port > 65535) {
        printf("Invalid port number: %d\n", port);
        return -1;
    }

    int32_t exitValue = -1;
    int32_t ret = 0;
    HITLS_Config *config = NULL;
    HITLS_Ctx *ctx = NULL;
    BSL_UIO *uio = NULL;
    int fd = 0;
    HITLS_X509_Cert *rootCA = NULL;
    HITLS_X509_Cert *subCA = NULL;
    struct addrinfo hints, *res = NULL;

    BSL_SAL_CallBack_Ctrl(BSL_SAL_MEM_MALLOC, malloc);
    BSL_SAL_CallBack_Ctrl(BSL_SAL_MEM_FREE, free);
    BSL_ERR_Init();

    ret = CRYPT_EAL_Init(CRYPT_EAL_INIT_CPU | CRYPT_EAL_INIT_PROVIDER);
    if (ret != CRYPT_SUCCESS) {
        printf("CRYPT_EAL_Init: error code is %x\n", ret);
        return ret;
    }

    ret = CRYPT_EAL_ProviderRandInitCtx(NULL, CRYPT_RAND_SHA256, "provider=default", NULL, 0, NULL);
    if (ret != CRYPT_SUCCESS) {
        printf("Init rand failed.\n");
        goto EXIT;
    }

    HITLS_CertMethodInit();
    HITLS_CryptMethodInit();

    fd = socket(AF_INET, SOCK_STREAM, 0);
    if (fd == -1) {
        printf("Create socket failed.\n");
        goto EXIT;
    }

    int option = 1;
    if (setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, &option, sizeof(option)) < 0) {
        close(fd);
        printf("setsockopt SO_REUSEADDR failed.\n");
        goto EXIT;
    }

    memset(&hints, 0, sizeof(hints));
    hints.ai_family = AF_INET; // IPv4
    hints.ai_socktype = SOCK_STREAM;

    char portStr[6];
    snprintf(portStr, sizeof(portStr), "%d", port);

    ret = getaddrinfo(host, portStr, &hints, &res);
    if (ret != 0 || res == NULL) {
        printf("getaddrinfo failed for host %s - %s\n", host, gai_strerror(ret));
        goto EXIT;
    }

    if (connect(fd, res->ai_addr, res->ai_addrlen) != 0) {
        printf("connect to %s:%d failed.\n", host, port);
        freeaddrinfo(res);
        goto EXIT;
    }

    config = HITLS_CFG_NewTLSConfig();
    if (config == NULL) {
        printf("HITLS_CFG_NewTLSConfig failed.\n");
        goto EXIT;
    }

    ret = HITLS_CFG_SetCheckKeyUsage(config, false);
    if (ret != HITLS_SUCCESS) {
        printf("Disable check KeyUsage failed.\n");
        goto EXIT;
    }

    ret = HITLS_CFG_SetVerifyNoneSupport(config, true);
    if (ret != HITLS_SUCCESS) {
        printf("Disable verify none support.\n");
        goto EXIT;
    }

    ctx = HITLS_New(config);
    if (ctx == NULL) {
        printf("HITLS_New failed.\n");
        goto EXIT;
    }

    uio = BSL_UIO_New(BSL_UIO_TcpMethod());
    if (uio == NULL) {
        printf("BSL_UIO_New failed.\n");
        goto EXIT;
    }

    ret = BSL_UIO_Ctrl(uio, BSL_UIO_SET_FD, sizeof(fd), &fd);
    if (ret != HITLS_SUCCESS) {
        BSL_UIO_Free(uio);
        printf("BSL_UIO_SET_FD failed, fd = %d.\n", fd);
        goto EXIT;
    }

    ret = HITLS_SetUio(ctx, uio);
    if (ret != HITLS_SUCCESS) {
        BSL_UIO_Free(uio);
        printf("HITLS_SetUio failed. ret = 0x%x.\n", ret);
        goto EXIT;
    }

    ret = HITLS_Connect(ctx);
    if (ret != HITLS_SUCCESS) {
        printf("HITLS_Connect failed, ret = 0x%x.\n", ret);
        goto EXIT;
    }

    const uint8_t sndBuf[] = "Hi, this is client\n";
    uint32_t writeLen = 0;
    ret = HITLS_Write(ctx, sndBuf, sizeof(sndBuf), &writeLen);
    if (ret != HITLS_SUCCESS) {
        printf("HITLS_Write error:error code:%d\n", ret);
        goto EXIT;
    }

    uint8_t readBuf[HTTP_BUF_MAXLEN + 1] = {0};
    uint32_t readLen = 0;
    ret = HITLS_Read(ctx, readBuf, HTTP_BUF_MAXLEN, &readLen);
    if (ret != HITLS_SUCCESS) {
        printf("HITLS_Read failed, ret = 0x%x.\n", ret);
        goto EXIT;
    }

    printf("get from server size:%u : %s\n", readLen, readBuf);
    exitValue = 0;

EXIT:
    if (res != NULL) {
        freeaddrinfo(res);
    }
    HITLS_Close(ctx);
    HITLS_Free(ctx);
    HITLS_CFG_FreeConfig(config);
    close(fd);
    HITLS_X509_CertFree(rootCA);
    HITLS_X509_CertFree(subCA);
    BSL_UIO_Free(uio);
    return exitValue;
}
