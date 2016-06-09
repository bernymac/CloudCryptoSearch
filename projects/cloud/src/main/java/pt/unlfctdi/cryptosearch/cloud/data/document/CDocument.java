/**
 *    Copyright 2013 Bernardo Luís da Silva Ferreira

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package pt.unlfctdi.cryptosearch.cloud.data.document;

import java.io.Serializable;

import pt.unlfctdi.cryptosearch.cloud.data.searchCipher.WordKey;

public class CDocument implements Serializable {
	
	private static final long serialVersionUID = -2229654345571204593L;

	private WordKey title;
	
	private WordKey[] content;
	
	public CDocument () { }
	
	public CDocument (WordKey title, WordKey[] content) {
		setTitle(title);
		setContent(content);
	}

	public void setTitle(WordKey title) {
		this.title = title;
	}

	public WordKey getTitle() {
		return title;
	}

	public void setContent(WordKey[] content) {
		this.content = content;
	}

	public WordKey[] getContent() {
		return content;
	}
	
}