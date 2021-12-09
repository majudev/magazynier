#!/bin/bash
_BAR="================================================================================"
VERSIONS=("v0.1.1-ALPHA")
echo $_BAR
echo "| List of available versions"
i=1
for v in ${VERSIONS[@]}; do
	echo "| [$i] $v"
	i=$((i+1))
done
echo "|"
echo "| Please enter version you want to download: "
read "choice"
if [[ $choice -ge $i ]]; then
	echo "Invalid choice."
	exit -1
elif [[ $choice -le 0 ]]; then
	echo "Invalid choice."
	exit -1
fi
URL="https://github.com/majudev/magazynier/releases/Magazynier-${VERSIONS[$((choice-1))]}.zip"
echo $URL

