package io.github.xyzeva.quilt_reflections.vfs;

import io.github.xyzeva.quilt_reflections.QuiltReflectionsException;
import org.quiltmc.loader.impl.filesystem.QuiltBasePath;
import org.quiltmc.loader.impl.filesystem.QuiltZipPath;
import org.reflections.vfs.Vfs;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class QuiltDir implements Vfs.Dir {
	QuiltZipPath root;
	Object fs;
	public QuiltDir(QuiltZipPath root) {
		this.root = root;
		try {
			this.fs = QuiltBasePath.class.getDeclaredMethod("getFileSystem").invoke(root);
		} catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
			throw new QuiltReflectionsException(e);
		}
	}

	@Override
	public String getPath() {
		return root.toString().replace("\\", "/");
	}

	@Override
	public Iterable<Vfs.File> getFiles() {
		try {
			return Files.walk(root).filter(Files::isRegularFile).map(a -> new QuiltFile((QuiltZipPath) a, fs)).collect(Collectors.toList());
		} catch (IOException e) {
			throw new QuiltReflectionsException(e);
		}
	}

}
