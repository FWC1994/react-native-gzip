# react-native-gzip
加压缩gzip格式文件
## Getting started

`$ npm install react-native-gzip --save`

### Mostly automatic installation

`$ react-native link react-native-gzip`

## Usage
```javascript
import Gzip from 'react-native-gzip';

// TODO: What to do with the module?
const sourcePath = `${somePath}/xxx.gz`
const targetPath = `${somePath}/xxx`

Gzip.gunzip(sourcePath, targetPath, true).then((res)=>{
    console.log(res)
})
```
gunzip params
|  params   | des  |  must  |
|  ----  | ----  |  ----  |
| sourcePath  | the path of .gz file |  true  |
| targetPath  | gunzip target path |  true  |
| force  | cover if exist |  true  |

