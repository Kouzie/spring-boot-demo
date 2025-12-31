#!/bin/bash
kcadm_wrapper() {
    /opt/keycloak/bin/kcadm.sh "$@" --config /tmp/kcadm.config
}
KCADM=kcadm_wrapper

# Login
$KCADM config credentials --server http://localhost:8080 --realm master --user admin --password admin

# Create Realm
$KCADM create realms -s realm=demo-realm -s enabled=true

# Create Client
$KCADM create clients -r demo-realm -s clientId=demo-client -s publicClient=false -s secret=demo-client-secret -s directAccessGrantsEnabled=true -s rootUrl=http://localhost:8081 -s baseUrl=http://localhost:8081 -s 'webOrigins=["+"]' -s 'redirectUris=["*"]'

# Create User
$KCADM create users -r demo-realm -s username=manager_user -s enabled=true -s email=manager@example.com
$KCADM set-password -r demo-realm --username manager_user --new-password password

# Create Role
$KCADM create roles -r demo-realm -s name=manager

# Assign Role
$KCADM add-roles -r demo-realm --uusername manager_user --rolename manager
# Assign view-users role from realm-management client to allow querying users
$KCADM add-roles -r demo-realm --uusername manager_user --cclientid realm-management --rolename view-users
