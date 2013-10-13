/**
 * Copyright (C) 2013, Moss Computing Inc.
 *
 * This file is part of pdf-renderers.
 *
 * pdf-renderers is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * pdf-renderers is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pdf-renderers; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package com.moss.pdf.genericrenderer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public abstract class AbstractPdfRendererTest {
	private final GenericPdfRenderer renderer;
	
	public AbstractPdfRendererTest(GenericPdfRenderer renderer) {
		super();
		this.renderer = renderer;
	}

	private GenericPdfDocument read(String name){
		try {
			byte[] pdf = read(getClass().getClassLoader().getResourceAsStream(name));
			return renderer.read(pdf);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void readsASinglePageDocument() throws Exception {
		final GenericPdfDocument d = read("test.pdf");
		assertEquals(1, d.numPages());
		final GenericPdfPage page = d.page(0);
		assertPage(792.0f, 792, 612.0f, 612, page, 0);
	}
	
	@Test
	public void readsAMultiPageDocument() throws Exception {
		final GenericPdfDocument d = read("test2.pdf");
		assertEquals(66, d.numPages());
		for(int x=0;x<d.numPages();x++){
			assertPage(612.0f, 612, 792.0f, 792, d.page(x), x);
		}
	}
	
    private void assertPage(float xf, int x, float yf, int y, GenericPdfPage page, int pageNum) throws Exception {
    	assertEquals(xf, page.width(), 0);
    	assertEquals(yf, page.height(), 0);
    	
    	RenderedPage r = page.render(new Dimension(x, y), 1);
		assertEquals(new Dimension(x, y), r.dimension);
		assertEquals(pageNum, r.page);
		assertNotNull(r.image);
		assertEquals(1.0f, r.zoom, 0);
	}

//	private void testPrint(GenericPdfRenderer r) throws Exception {
//		
////		MultiDocPrintService[] services = PrintServiceLookup.lookupMultiDocPrintServices(null, null);
//		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
////		r.print(pdf, "Test PDF " + UUID.randomUUID(), services[3]);
//	}
//	
	private byte[] read(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024*100];
		for(int numRead = in.read(buffer);numRead!=-1;numRead = in.read(buffer)){
			out.write(buffer, 0, numRead);
		}
		out.close();
		return out.toByteArray();
	}
}
