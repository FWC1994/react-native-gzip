#import "Gzip.h"

@interface Gzip ()

@end

@implementation Gzip

RCT_EXPORT_MODULE()

RCT_REMAP_METHOD(gunzip,
                 filePath: (NSString *) source
                 toFolder: (NSString *) folder
                 force: (BOOL) force
                 resolver: (RCTPromiseResolveBlock)resolve
                 rejecter: (RCTPromiseRejectBlock)reject)
{
    NSFileManager *manager = [NSFileManager defaultManager];

    if (![manager fileExistsAtPath:source]) {
        reject(@"-2", @"file not found", nil);
        return;
    }

    if ([manager fileExistsAtPath:folder]) {
        if (!force) {
            reject(@"-2", @"folder exists", nil);
            return;
        }
        NSError *unlinkError;
        if (![manager removeItemAtPath:folder error:&unlinkError]) {
            reject([@(unlinkError.code) stringValue], unlinkError.localizedDescription, unlinkError);
            return;
        }
    }

    // TODO Passing an error here results in EXC_BAD_ACCESS because the error is released
    if (![DCTar decompressData:[manager contentsAtPath:source] toPath:folder error:nil]) {
       [[NVHTarGzip sharedInstance] unTarFileAtPath:source toPath:folder completion:^(NSError* tarError) {
            if (tarError != nil) {
                NSLog(@"Error untarring %@", tarError);
                 reject(@"-2", @"untar error", nil);
            }else{
                 resolve(@{@"path": folder});
            }
           
        }];
    }else{
         resolve(@{@"path": folder});
    }

   
}

@end
