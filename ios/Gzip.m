#import "Gzip.h"

@interface Gzip ()

@end

@implementation Gzip

RCT_EXPORT_MODULE()

RCT_REMAP_METHOD(unGzip,
                 unGzip: (NSString *) source
                 toFolder: (NSString *) target
                 force: (BOOL) force
                 resolver: (RCTPromiseResolveBlock)resolve
                 rejecter: (RCTPromiseRejectBlock)reject)
{
    if(![self checkDir:source target:target force:force]) {
        reject(@"-2", @"error", nil);
        return;
    };
    
    [[NVHTarGzip sharedInstance] unGzipFileAtPath:source toPath:target completion:^(NSError *unGzipError) {
        if (unGzipError != nil) {
            reject(@"-2", @"ungzip error", nil);
            return;
        }
        resolve(@{@"path": target});
        return;
    }];
}

RCT_REMAP_METHOD(unTar,
                 unTar: (NSString *) source
                 toFolder: (NSString *) target
                 force: (BOOL) force
                 resolver: (RCTPromiseResolveBlock)resolve
                 rejecter: (RCTPromiseRejectBlock)reject)
{
    if(![self checkDir:source target:target force:force]) {
        reject(@"-2", @"error", nil);
        return;
    };
    
    [[NVHTarGzip sharedInstance] unTarFileAtPath:source toPath:target completion:^(NSError *unTarError) {
        if (unTarError != nil) {
            reject(@"-2", @"untar error", nil);
            return;
        }
        resolve(@{@"path": target});
        return;
    }];
}

RCT_REMAP_METHOD(unGzipTar,
                 unGzipTar: (NSString *) source
                 toFolder: (NSString *) target
                 force: (BOOL) force
                 resolver: (RCTPromiseResolveBlock)resolve
                 rejecter: (RCTPromiseRejectBlock)reject)
{
    if(![self checkDir:source target:target force:force]) {
        reject(@"-2", @"error", nil);
        return;
    };
    NSString *temporaryPath = [self temporaryFilePathForPath:source];
    [[NVHTarGzip sharedInstance] unGzipFileAtPath:source toPath:temporaryPath completion:^(NSError *gzipError) {
        if (gzipError != nil) {
            reject(@"-2", @"ungzip error", nil);
            return;
        }
        
        [[NVHTarGzip sharedInstance] unTarFileAtPath:temporaryPath toPath:target completion:^(NSError *tarError) {
            NSError* error = nil;
            [[NSFileManager defaultManager] removeItemAtPath:temporaryPath error:&error];
            if (tarError != nil) {
                error = tarError;
                reject(@"-2", @"untar error", nil);
                return;
            }
            resolve(@{@"path": target});
            return;
        }];
    }];
}

- (Boolean)checkDir:(NSString *)source
                 target:(NSString *)target
                  force:(Boolean)force {
    NSFileManager *manager = [NSFileManager defaultManager];

    if (![manager fileExistsAtPath:source]) {
        return NO;
    }

    if ([manager fileExistsAtPath:target]) {
        if (!force) {
            return NO;
        }
        NSError *unlinkError;
        if (![manager removeItemAtPath:target error:&unlinkError]) {
            return NO;
        }
    }
    return YES;
}


- (NSString *)temporaryFilePathForPath:(NSString *)path {
    NSString *UUIDString = [[NSUUID UUID] UUIDString];
    NSString *filename = [[path lastPathComponent] stringByDeletingPathExtension];
    NSString *temporaryFile = [filename stringByAppendingFormat:@"-%@",UUIDString];
    NSString *temporaryPath = [NSTemporaryDirectory() stringByAppendingPathComponent:temporaryFile];
    if (![[temporaryPath pathExtension] isEqualToString:@"tar"]) {
        temporaryPath = [temporaryPath stringByAppendingPathExtension:@"tar"];
    }
    return temporaryPath;
}

@end
