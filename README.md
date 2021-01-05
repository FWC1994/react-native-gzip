# react-native-gzip
解压缩gzip和tar格式文件
- IOS实现基于[NVHTarGzip](https://github.com/nvh/NVHTarGzip)
- Android实现基于[CompressorStreamFactory](https://commons.apache.org/proper/commons-compress/apidocs/org/apache/commons/compress/compressors/CompressorStreamFactory.html)和[ArchiveStreamFactory](https://commons.apache.org/proper/commons-compress/javadocs/api-1.18/org/apache/commons/compress/archivers/ArchiveStreamFactory.html)

## 安装

```
npm install @fengweichong/react-native-gzip --save

ios -> pod install
```
## 使用
```javascript
import Gzip from '@fengweichong/react-native-gzip';

const sourcePath = `${PATH}/xxx.gz`
const targetPath = `${PATH}/xxx`

// 解压缩tar
Gzip.unTar(sourcePath, targetPath, true).then((res)=>{
    console.log(res)
})

// 解压缩gzip
Gzip.unGzip(sourcePath, targetPath, true).then((res)=>{
    console.log(res)
})

// 解压缩gzip和tar
Gzip.unGzipTar(sourcePath, targetPath, true).then((res)=>{
    console.log(res)
})
```
### 参数
|  名称   | 说明  |  会否必须  |
|  ----  | ----  |  ----  |
| sourcePath  | 目标文件地址 |  true  |
| targetPath  | 解压目标地址 |  true  |
| force  | 是否覆盖目标地址 |  true  |

