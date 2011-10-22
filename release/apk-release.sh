# if you want to do this script, you should run 'export -f gettop'
SIGNAPK_DIR=`gettop`/out/host/linux-x86/framework
KEY_DIR=`gettop`/build/target/product/security
java -jar $SIGNAPK_DIR/signapk.jar $KEY_DIR/private.x509.pem $KEY_DIR/private.pk8 OpenWnn4T-release-unsigned.apk OpenWnn4T-release-unaligned.apk
zipalign -f -v 4 OpenWnn4T-release-unaligned.apk OpenWnn4T.apk
