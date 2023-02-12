import io.xpipe.ext.base.*;
import io.xpipe.ext.base.actions.*;
import io.xpipe.ext.base.apps.*;
import io.xpipe.extension.DataSourceProvider;
import io.xpipe.extension.DataSourceTarget;
import io.xpipe.extension.DataStoreProvider;
import io.xpipe.extension.util.ActionProvider;

open module io.xpipe.ext.base {
    exports io.xpipe.ext.base;
    exports io.xpipe.ext.base.apps;
    exports io.xpipe.ext.base.actions;

    requires java.desktop;
    requires io.xpipe.core;
    requires io.xpipe.extension;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires static lombok;
    requires static javafx.controls;
    requires static net.synedra.validatorfx;
    requires static io.xpipe.app;

    provides ActionProvider with
            AddStoreAction,
            EditStoreAction,
            StreamExportAction,
            ShareStoreAction,
            FileBrowseAction,
            FileEditAction;
    provides DataSourceTarget with
            SaveSourceTarget,
            JavaTarget,
            CommandLineTarget,
            FileOutputTarget,
            DataSourceOutputTarget,
            RawFileOutputTarget;
    provides DataSourceProvider with
            TextSourceProvider,
            BinarySourceProvider,
            XpbtProvider,
            XpbsProvider;
    provides DataStoreProvider with
            SinkDrainStoreProvider,
            HttpStoreProvider,
            LocalStoreProvider,
            InternalStreamProvider,
            FileStoreProvider,
            InMemoryStoreProvider;
}
