#!bin/sh

# HOW TO RUN THIS SCRIPT
# 1. unzip the bin-release file.
# 2. place this script under your unzipped bin-release directory.
# 3. if you want to check the javascript dependencies, 
#    copy the package.json file to the directory.
# 4. run this script.

# Step 1: check all the dependendcies list in LICENSE are acutally bundled.

echo "Step 1: check all the dependendcies list in LICENSE are acutally bundled"
echo "================"

files=($(grep jar LICENSE | awk -F ' '  '{ print $2 }'))

for i in "${files[@]}"; do
  file=$(echo $i | tr -d '\r')
  if [ ! -f "BOOT-INF/lib/$file" ]; then
    echo "jar file $file is listed in LICENSE but not found in BOOT-INF/lib."
  fi
done

echo "\n"

# Step 2: check all the jar files under BOOT-INF/lib are listed in LICENSE.
echo "Step 2: check all the jar files under BOOT-INF/lib are listed in LICENSE."
echo "================"

for i in BOOT-INF/lib/*.jar; do
  jar=$(echo $i | awk -F '/' '{print $3}')
  if [ $(grep -c $jar LICENSE) -eq 0 ]; then
    echo "Found $i but not listed in LICENSE"
  fi
done

echo "\n"

# Step 3: if a project is Apache Licensed and has NOTICE, it should be listed in NOTICE
echo "Step 3: if a project is Apache Licensed and has NOTICE, it should be listed in NOTICE"
echo "================="

licenses=($(echo $(grep "Apache 2.0" LICENSE | awk -F ' ' '{if ($0 ~ /^.*\*/) printf "%s,%s\\n",$3,$5}')))
for i in "${licenses[@]}"; do
  name=$(echo $i | awk -F ',' '{print $1}')
  # echo "checking $name..."
  license=$(echo $i | awk -F ',' '{print $2}' | awk '{if (toupper($0) ~ /.*LICENSE.*/) print $0}')
  # remove the trailing LICENSE.txt
  notice="${license%/*}/NOTICE"
  noticeTxt="$notice.txt"
  if [ $(curl --write-out %{http_code} --head --output /dev/null -s $notice | grep -c 200) -ne 0 ] \
     || [ $(curl --write-out %{http_code} --head --output /dev/null -s $noticeTxt | grep -c 200) -ne 0 ]; then
    # echo "found Notice file, checking it is listed in NOTICE"
    if [ $(grep -c $name NOTICE) -eq 0 ]; then
      echo "$name should be added to NOTICE"
    fi
  fi
done

# Step 4: check javascript dependencies, ensure they are listed in LICENSE.
# TODO
