package github.mobai.compress;

import github.mobai.extension.SPI;

/**
 * 解压缩
 *
 * @author mobai
 * @date 2022/3/2
 */
@SPI
public interface Compress {

    byte[] compress(byte[] bytes);

    byte[] decompress(byte[] bytes);
}
