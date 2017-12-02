#!/bin/bash
apt-get install gnupg2
cd setup

curl https://keybase.io/jrtapsell/pgp_keys.asc | gpg --import
curl https://github.com/web-flow.gpg | gpg --import
gpg --import-ownertrust trusts.txt

gpg --gen-key --batch setup.gpg

fingerprint=$(gpg --fingerprint example@example.com | grep "=" | cut -d "=" -f 2 | tr -d ' ')

cd ..
