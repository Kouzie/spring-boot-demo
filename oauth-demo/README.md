```shell

curl -X POST "http://localhost:8080/register" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer your_initial_access_token" \
  -d '{
        "client_name": "client-1",
        "grant_types": ["authorization_code"],
        "redirect_uris": ["https://client.example.org/callback", "https://client.example.org/callback2"],
        "logo_uri": "https://client.example.org/logo",
        "contacts": ["contact-1", "contact-2"],
        "scope": "openid email profile"
      }'

curl -X GET "http://localhost:8080/your-registration-client-uri" \
  -H "Authorization: Bearer your_registration_access_token"

```