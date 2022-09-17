#! /bin/sh

VERSION=$1

if [ -z "$VERSION" ]; then
    echo "Failed: Target version is empty"; exit 1
fi

if ! grep -q "VERSION_NAME=$VERSION-SNAPSHOT" gradle.properties; then
    echo "Failed: Target version '$VERSION' does not match"; exit 1
fi

echo "Rewriting version $VERSION-SNAPSHOT to $VERSION for release"

sed -i.old "/VERSION_NAME=/s/$VERSION-SNAPSHOT/$VERSION/" gradle.properties
