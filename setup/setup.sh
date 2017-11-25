#!/bin/bash
cd setup
gpg --import j1.asc
gpg --import j2.asc
gpg --import-ownertrust trusts.txt

gpg --gen-key --batch setup.gpg
cd ..
