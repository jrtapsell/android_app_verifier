#!/bin/bash
apt-get install gnupg2
cd setup

gpg --import j1.asc
gpg --import j2.asc
gpg --import-ownertrust trusts.txt

gpg --gen-key --batch setup.gpg

fingerprint=$(gpg --fingerprint example@example.com | grep "=" | cut -d "=" -f 2 | tr -d ' ')
#gpg-agent --daemon --verbose --allow-preset-passphrase

#/usr/lib/gnupg2/gpg-preset-passphrase --preset -P password $fingerprint
alias gpg="gpg --passphrase password"
cd ..
