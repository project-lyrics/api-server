aws secretsmanager update-secret --secret-id /lyrics/server/$1 --secret-string file://secrets.$1.json
