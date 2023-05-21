package io.github.xyzeva.quilt_reflections.vfs;

import io.github.xyzeva.quilt_reflections.QuiltReflectionsException;
import org.quiltmc.loader.impl.filesystem.QuiltZipPath;
import org.reflections.vfs.Vfs;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;


public class QuiltFile implements Vfs.File {
	QuiltZipPath path;
	Object fs;
	public QuiltFile(QuiltZipPath path, Object fs) {
		this.path = path;
		this.fs = fs;
	}
	@Override
	public String getName() {
		return path.getFileName().toString();
	}

	@Override
	public String getRelativePath() {
		try {
			String stringPath = (String) Path.class.getDeclaredMethod("toString").invoke(path);
			if (stringPath.charAt(0) == '/') {
				stringPath = stringPath.substring(1);
			}
			return stringPath;
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new QuiltReflectionsException(e);
		}
	}

	@Override
	public InputStream openInputStream() {
		try {
			Object provider = fs.getClass().getDeclaredMethod("provider").invoke(fs);
			return (InputStream) provider.getClass().getDeclaredMethod("newInputStream", Path.class, OpenOption[].class)
					.invoke(
							provider,
							path,
							new OpenOption[] { StandardOpenOption.READ }
					);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new QuiltReflectionsException(e);
		}
	}
}
