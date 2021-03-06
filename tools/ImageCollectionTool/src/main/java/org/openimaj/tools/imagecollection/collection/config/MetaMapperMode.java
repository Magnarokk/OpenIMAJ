/**
 * Copyright (c) 2011, The University of Southampton and the individual contributors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * 	Redistributions of source code must retain the above copyright notice,
 * 	this list of conditions and the following disclaimer.
 *
 *   *	Redistributions in binary form must reproduce the above copyright notice,
 * 	this list of conditions and the following disclaimer in the documentation
 * 	and/or other materials provided with the distribution.
 *
 *   *	Neither the name of the University of Southampton nor the names of its
 * 	contributors may be used to endorse or promote products derived from this
 * 	software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openimaj.tools.imagecollection.collection.config;

import java.io.File;
import java.io.IOException;

import org.kohsuke.args4j.Option;
import org.openimaj.image.MBFImage;
import org.openimaj.tools.imagecollection.metamapper.ConsoleMetaMapper;
import org.openimaj.tools.imagecollection.metamapper.FileMetaMapper;
import org.openimaj.tools.imagecollection.metamapper.MetaMapper;
import org.openimaj.tools.imagecollection.processor.DirectoryImageProcessor;
import org.openimaj.tools.imagecollection.processor.ImageCollectionProcessor;

public enum MetaMapperMode {
	CONSOLE{
		@Override
		public MetaMapper mapper(ImageCollectionProcessor<MBFImage> processor) {
			return new ConsoleMetaMapper();
		}
		
	},
	FILE{
		@Option(name="--meta-output-name", aliases="-mn", required=false, usage="The name of the meta output file", metaVar="STRING")
		private String outputName = "meta.txt";
		
		@Option(name="--directory-mode-root", aliases="-mdr", required=false, usage="If the output mode is directory, put the meta file in the directory", metaVar="STRING")
		private boolean directoryModeRoot = true;
		
		@Option(name="--meta-output-root", aliases="-mor", required=false, usage="If not using the directory mode root, the root folder", metaVar="STRING")
		private String actualRoot= "./metaout";
		@Override
		public MetaMapper mapper(ImageCollectionProcessor<MBFImage> processor) throws IOException {
			String actualOutDirectory = actualRoot;
			if(directoryModeRoot && processor instanceof DirectoryImageProcessor){
				DirectoryImageProcessor<?> dproc = ((DirectoryImageProcessor<?>)processor);
				actualOutDirectory = dproc.getDirectoryFile().getAbsolutePath();
			}
			
			File outFile = new File(actualOutDirectory,outputName);
			
			return new FileMetaMapper(outFile);
			
		}
		
	};
	public abstract MetaMapper mapper(ImageCollectionProcessor<MBFImage> processor) throws IOException;
}