#!/usr/bin/env bash
NAME=${NAME:-pttg-ip-api}

JAR=$(find . -name ${NAME}*.jar|head -1)
java ${JAVA_OPTS} -Dcom.sun.management.jmxremote.local.only=false -Djava.security.egd=file:/dev/./urandom -jar "${JAR}"

#
#certfiles=$(awk '/-----BEGIN CERTIFICATE-----/{filename="ebsaca"NR; print filename}; {print >filename}' /mnt/certs/vault_pki_ca.crt)
#
#for file in ${certfiles}
#do
#keytool -import -alias "${file}" -file "${file}" -keystore ./truststore.jks -noprompt -storepass changeit -trustcacerts
#rm "${file}"
#done
#
#keytool -importkeystore -destkeystore /app/truststore.jks -srckeystore /opt/jdk/jre/lib/security/cacerts -srcstorepass changeit -noprompt -storepass changeit &> /dev/null
#
#java ${JAVA_OPTS} -Djavax.net.ssl.trustStore=/app/truststore.jks \
#                  -Dcom.sun.management.jmxremote.local.only=false \
#                  -Djava.security.egd=file:/dev/./urandom -jar "${JAR}" | tee ${LOGFILE} 2>&1
