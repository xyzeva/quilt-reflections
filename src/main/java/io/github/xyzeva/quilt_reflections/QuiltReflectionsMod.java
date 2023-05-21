package io.github.xyzeva.quilt_reflections;

import io.github.xyzeva.quilt_reflections.vfs.QuiltDir;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.impl.filesystem.QuiltZipPath;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.reflections.vfs.Vfs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;

public class QuiltReflectionsMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Quilt Reflections");

	public static Class<?> QUILT_ZFS_PROVIDER;
	static {
		try {
			QUILT_ZFS_PROVIDER = Class.forName("org.quiltmc.loader.impl.filesystem.QuiltZipFileSystemProvider");
			addQuiltFileSystem();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	/* no-op */ public void onInitialize(ModContainer mod) {}

	public static void addQuiltFileSystem() {
		LOGGER.info("Adding quilt ZFS");
		Vfs.addDefaultURLTypes(new Vfs.UrlType() {
			@Override
			public boolean matches(URL url) {
				return "quilt.zfs".equals(url.getProtocol()) && Vfs.getFile(url).isDirectory();
			}
			@Override
			public Vfs.Dir createDir(URL url) throws Exception {
				return new QuiltDir((QuiltZipPath) QUILT_ZFS_PROVIDER.getDeclaredMethod("getPath", URI.class).invoke(QUILT_ZFS_PROVIDER.getDeclaredMethod("instance").invoke(null), url.toURI()));
			}
		});
	}
}
