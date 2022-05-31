#!/bin/sh
# This script is used in the final image to start an openssl server or client

show_help()
{
  echo "Usage:"
  echo "  server mode: '$0 [-d <output_directory>] server'"
  echo "  client mode: '$0 [-d <output_directory>] client <host>:<port>'"
}

exit_with_error() 
{
    printf '%s\n' "$1" >&2
    show_help
    exit 1
}

directory=
server_mode=false
client_mode=false
client_dest=

# ===== Parse and Check Arguments =====

while :; do
    case $1 in
        -h|-\?|--help)
            show_help    
            exit
            ;;
        -d|--directory)       
            if [ "$2" ]; then
                directory=$2
                shift
            else
                exit_with_error '[ERROR] "-d/--directory" requires a non-empty option argument.'
            fi
            ;;
        server)
            server_mode=true  
            ;;
        client)
             if [ "$2" ]; then
                client_mode=true
                client_dest=$2
                shift
            else
                exit_with_error "[ERROR] Client mode requires the server destination in form 'client <host>:<port>'"
            fi
            ;;
        *)              
            if [ "$1" ]; then
              printf '[WARN] Unknown option (ignored): %s\n' "$1" >&2
            else
              break
            fi
            ;;

    esac
    shift
done

if [ "$server_mode" = false ] && [ "$client_mode" = false ] ; then
    exit_with_error "[ERROR] Server or client mode needs to be specified."
fi

if [ "$server_mode" = true ] && [ "$client_mode" = true ] ; then
    exit_with_error "[ERROR] Server and client mode must not be specified simultaneously."
fi

# ===== Execution =====

# Handle docker stop (sending SIGTERM)
terminated=false
_term() {
    echo "Caught SIGTERM signal!" 
    terminated=true
    kill -15 "$child" 2>/dev/null
}
trap _term TERM

# --- Execute the server/client ---

cd "/src/openssl/"

if [ "$server_mode" = true ]
then
    echo "Start OpenSSL Server."
    /bin/openssl-server-entrypoint ./apps/openssl s_server -accept 4433 -key /cert/ec256key.pem -cert /cert/ec256cert.pem -comp &
    child=$!
    wait "$child"
    wait "$child"
elif [ "$client_mode" = true ]
then
    echo "Start OpenSSL Client."
    /bin/openssl-client-entrypoint ./apps/openssl s_client -connect $client_dest -comp &
    child=$!
    wait "$child"
    wait "$child"
fi

if [ "$terminated" = true ] ; then
    echo "Entrypoint was terminated with SIGTERM (probably from 'docker stop'). Coverage data is not processed yet."
    exit 0
fi

# --- Collect the coverage data ---

echo "Process coverage data..."

LCOV_DIR=lcov-results
LCOV_FILE=coverage_tmp.info
LCOV_FILTERED=/usr/info/coverage.info

lcov --capture --directory . --output-file $LCOV_FILE
lcov -r $LCOV_FILE /usr/include/\* --output-file $LCOV_FILTERED

# --- Store the coverage data in the docker volume ---

cd "/covVolume/"

if [ -z "$directory" ] ; then
    # No directory was specified. Create a generic one.
    max=`ls -1d results* | tr -dc '[0-9\n]' | sort -k 1,1n | tail -1`
    directory=results_$((max + 1))
fi
mkdir -p $directory

cd $directory
cp /usr/info/* .

exit 0