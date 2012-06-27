VERSION=1.0.0-SNAPSHOT

function copy {
  MODULE="$1"
  BUNDLE="$MODULE-$VERSION.jar"
  cp $MODULE/target/$BUNDLE ../osgi/equinox/bundles
  echo "bundles/$BUNDLE@start, \\" >> ../osgi/equinox/config/config.ini  
}

cp ../osgi/equinox/config-template/config.ini ../osgi/equinox/config/config.ini

copy cloud-disco-framework-service
copy cloud-disco-services
copy cloud-disco-zookeeper-plugin
copy cloud-disco-demo-api
copy cloud-disco-demo-provider
copy cloud-disco-demo-web-ui

echo " " >> ../osgi/equinox/config/config.ini  

