package br.senai.sp.cfp138.guideresta.util;

import java.io.IOException;
import java.util.UUID;import org.hibernate.result.NoMoreReturnsException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Service
public class FirebaseUtil {
	// variável para guardar as credenciais de acesso
	private Credentials credenciais;
	// variável para acessar e manipular o storage
	private Storage storage;
	// constante para o nome do bucket
	private final String BUCKET_NAME = "restaguide-chile.appspot.com";
	// constante para o prefixo da URL
	private final String PREFIX = 
			"https://firebasestorage.googleapis.com/v0/b/"+BUCKET_NAME+"/o/";
	// constante para o sufixo da URL
	private final String SUFFIX = "?alt=media";
	// constante para a URL
	private final String DOWNLOAD_URL = PREFIX + "%s" + SUFFIX;
	
	public FirebaseUtil() {
		// acessar o arquivo json com a chave privada
		Resource resource = new ClassPathResource("chavefirebase.json");	
		try {
			// gera uma credencial no Firebase através da chave do arquivo
			credenciais = GoogleCredentials.fromStream(resource.getInputStream());
			// cria o storage para manipular os dados no Firebase
			storage = StorageOptions.newBuilder().
							setCredentials(credenciais).build().getService();
			
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	// método para extrair a extensão do arquivo
	private String getExtensao(String nomeArquivo) {
		// extrai o trecho do arquivo onde está a extensão
		return nomeArquivo.substring(nomeArquivo.lastIndexOf('.'));
	}
	
	// método que faz o upload
	public String upload(MultipartFile arquivo) throws IOException {
		// gera um nome aleatório para o arquivo
		String nomeArquivo = UUID.randomUUID().toString() 
						+ getExtensao(arquivo.getOriginalFilename());
		// criar um blobId através do nome gerado para o arquivo
		BlobId blobId = BlobId.of(BUCKET_NAME, nomeArquivo);
		// criar um blobInfo através do blobId
		BlobInfo blobInfo = 
				BlobInfo.newBuilder(blobId).setContentType("media").build();
		// gravar o blobinfo no Storage passando os bytes do arquivo
		storage.create(blobInfo, arquivo.getBytes());
		// retorna a URL do arquivo gerado no Storage
		return String.format(DOWNLOAD_URL, nomeArquivo);
	}
	
	// método que exclui o arquivo do storage
	public void deletar(String nomeArquivo) {
		// retirar o prefixo e o sufixo da String
		nomeArquivo = nomeArquivo.replace(PREFIX, "").replace(SUFFIX, "");
		// obter um Blob através do nome
		Blob blob = storage.get(BlobId.of(BUCKET_NAME, nomeArquivo));
		// deleta através do blob
		storage.delete(blob.getBlobId());
	}
}
