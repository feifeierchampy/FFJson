#!/usr/bin/env bash
# This file is used to create current major using marjor version and build number.

ver_num_tag=$(git describe --tags --abbrev=0)
# ver_num_sub=$(git rev-list --tags $ver_num_tag..HEAD --count)
commit_hash=$(git show --pretty=format:%h -s)
version=$ver_num_tag.$commit_hash
#buildNumber=${BUILD_NUMBER}
#if [[ -z $buildNumber ]]; then
#    buildNumber=$(whoami)
#fi

echo "$version"
