#! /usr/bin/env bash
script_dir=$(cd `dirname $0`; pwd)
echo  "$script_dir"

BUILD_OUTPUT_DIR=${script_dir}/output

ANNOTATION_BUILD_DIR=${script_dir}/json-annotation/build/libs
ANNOTATION_PROCESSOR_BUILD_DIR=${script_dir}/json-annotation-processor/build/libs

rm -rf ${BUILD_OUTPUT_DIR}
mkdir -p ${BUILD_OUTPUT_DIR}

find ${script_dir} -type d -name "build" | xargs rm -rf

./gradlew jar -p json-annotation
./gradlew jar -p json-annotation-processor

version_name=`sh tools/generate-version.sh`
echo "${version_name}"

cp ${ANNOTATION_BUILD_DIR}/json-annotation.jar ${BUILD_OUTPUT_DIR}/json-annotation-${version_name}.jar
cp ${ANNOTATION_PROCESSOR_BUILD_DIR}/json-annotation-processor.jar ${BUILD_OUTPUT_DIR}/json-annotation-processor-${version_name}.jar





