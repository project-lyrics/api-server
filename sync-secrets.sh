aws secretsmanager get-secret-value --secret-id /lyrics/server/$1 --output json | jq -r '.SecretString' > secrets.$1.json
