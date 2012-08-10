VERSION=1.0.0-SNAPSHOT
TARGET=$1

function copy {
  MODULE="$1"
  BUNDLE="$MODULE-$VERSION.jar"
  cp $MODULE/target/$BUNDLE $TARGET/cloud-prov-demo/src/main/resources
}

if [ ! -e $TARGET/cloud-prov-demo/src/main/resources ] 
then
    echo Directory structure not as expected in $TARGET
    echo Expected at least an cloud-prov-demo/src/main/resources directory in that location
    exit
fi

copy cloud-disco-demo-api
copy cloud-disco-demo-provider 
copy cloud-disco-demo-web-ui


