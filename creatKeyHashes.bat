keytool -exportcert -alias npc -keystore KeyNPC | openssl sha1 -binary | openssl base64