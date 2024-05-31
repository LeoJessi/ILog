package top.jessi.ilog.printer.file.clean;

import java.io.File;

/**
 * Never Limit the file life.
 *
 * @since 1.5.0
 */
public class NeverCleanStrategy implements CleanStrategy {

    @Override
    public boolean shouldClean(File file) {
        return false;
    }
}
