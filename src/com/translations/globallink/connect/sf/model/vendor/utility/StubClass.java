package com.translations.globallink.connect.sf.model.vendor.utility;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.translations.globallink.connect.sf.model.vendor.dto.SFArticle;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleField;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleType;
import com.translations.globallink.connect.sf.model.vendor.dto.SFConnectionConfig;
import com.translations.globallink.connect.sf.model.vendor.dto.SFLocale;
import com.translations.globallink.connect.sf.model.vendor.dto.SFQueue;
import com.translations.globallink.connect.sf.model.vendor.dto.SFUser;
import com.translations.globallink.connect.sf.model.vendor.service.SFKnowledgeArticleService;
import com.translations.globallink.connect.sf.model.vendor.service.impl.SFKnowledgeArticleServiceImpl;

/**
 * @class Name-SFUtilityMainClass this is Class which contains main() method to run and execute all methoed inside it.
 * 
 * @author-
 * 
 * @createdDate-
 */
public class StubClass {

    /**
     * @throws Exception
     */

    public static void main(String[] args) throws Exception {
	//	callgetReadyArticleIdsForTranslation();//after translation
	//	callgetArticleStreamForTranslation();// before transaltion
	//	callimportTranslatedArticle();
	//	callgetSFQueues();
	//	callgetSFLocales();
	//	callgettype();
	//	callgetFieldsForArticleType();
	//	calltestConnection();
	//	callsetAssignee();
	//	callgetUsers();

    }

    public static SFConnectionConfig getLoginDetailsFromMiddleware() {
	SFConnectionConfig loginDetailBean = new SFConnectionConfig();
	loginDetailBean.setUser("Nakul@ka.dev");
	loginDetailBean.setPassword("test@123ELikr4MyfI1xfgtYZdCyxeTnv");
	loginDetailBean.setConsumerKey("3MVG9ZL0ppGP5UrBR.600kPJSKldTpds6SxCgEgj44lSgMJAcU9C5etNsi9y5GusxXFEuSKd3m3oylBiXdNtR");
	loginDetailBean.setConsumerSecret("5127982414795005574");
	loginDetailBean.setUrl("https://ap2.salesforce.com");
	//loginDetailBean.setQueueId("00G28000000U3LoEAK");
	return loginDetailBean;
    }

    // Dummy code
    /**
     * hard code data insertion in the SFArticleField-custum fileds
     * 
     * @return
     */
    public static List<SFArticleField> getSFArticleCustomFieldList() {
	List<SFArticleField> fields = new ArrayList<SFArticleField>();
	fields.add(new SFArticleField("Name__c", "name", "Text", 256, true));
	fields.add(new SFArticleField("Summary", "Summary", "Text", 1026, true));
	fields.add(new SFArticleField("Title", "Title", "Text", 256, true));
	return fields;

    }

    public static void callgetUsers() throws Exception {
	SFConnectionConfig sfConnectionConfig = getLoginDetailsFromMiddleware();
	SFKnowledgeArticleService sfKnowledgeArticleImpl = new SFKnowledgeArticleServiceImpl(sfConnectionConfig);
	List<SFUser> users = sfKnowledgeArticleImpl.getUsers();
	for (SFUser user : users) {
	    System.out.println("Id  " + user.getUserId() + "Name  " + user.getUserName());
	}

    }

    public static void callsetAssignee() throws Exception {
	SFConnectionConfig sfConnectionConfig = getLoginDetailsFromMiddleware();
	SFKnowledgeArticleService sfKnowledgeArticleImpl = new SFKnowledgeArticleServiceImpl(sfConnectionConfig);
	SFArticle sfarticle = new SFArticle();
	sfarticle.setId("ka0280000005XFKAA2");

	sfKnowledgeArticleImpl.setAssignee(sfarticle, "00528000001d2Ui");
    }

    public static void calltestConnection() throws Exception {
	SFConnectionConfig sfConnectionConfig = getLoginDetailsFromMiddleware();
	SFKnowledgeArticleService sfKnowledgeArticleImpl = new SFKnowledgeArticleServiceImpl(sfConnectionConfig);
	boolean testconnection = sfKnowledgeArticleImpl.testConnection(sfConnectionConfig);
	if (testconnection) {
	    System.out.println("Succesfully login!!!!");
	} else {
	    System.out.println("login Fail!!!!");
	}

    }

    public static void callgetFieldsForArticleType() throws Exception {
	SFConnectionConfig sfConnectionConfig = getLoginDetailsFromMiddleware();
	SFKnowledgeArticleService sfKnowledgeArticleImpl = new SFKnowledgeArticleServiceImpl(sfConnectionConfig);
	List<SFArticleField> sfTypefields = sfKnowledgeArticleImpl.getFieldsForArticleType("offer__kav");
	for (SFArticleField sfarticle : sfTypefields) {
	    if (!(sfarticle.getName().contentEquals("UrlName")) && !(sfarticle.getName().contentEquals("ArticleNumber"))) {
		System.out.println("name " + sfarticle.getName() + "  label " + sfarticle.getLabel() + "  Lenght " + sfarticle.getLength() + "  Type " + sfarticle.getType());
	    }

	}

    }

    public static void callgettype() throws Exception {
	SFConnectionConfig sfConnectionConfig = getLoginDetailsFromMiddleware();
	SFKnowledgeArticleService sfKnowledgeArticleImpl = new SFKnowledgeArticleServiceImpl(sfConnectionConfig);
	List<SFArticleType> sfTypes = sfKnowledgeArticleImpl.getArticleTypes();
	for (SFArticleType sfArticle : sfTypes) {
	    System.out.println(sfArticle.getLabel() + "======" + sfArticle.getName());
	    if (sfArticle.getFields() != null)
		for (SFArticleField sf : sfArticle.getFields()) {
		    System.out.println(sf.getLabel() + "======" + sf.getLength() + "======" + sf.getName() + "====" + sf.getType());

		}
	}

    }

    public static void callgetSFLocales() throws Exception {
	SFConnectionConfig sfConnectionConfig = getLoginDetailsFromMiddleware();
	SFKnowledgeArticleService sfKnowledgeArticleImpl = new SFKnowledgeArticleServiceImpl(sfConnectionConfig);
	List<SFLocale> lang = sfKnowledgeArticleImpl.getSFLocales();
	for (SFLocale locale : lang) {
	    System.out.println("Id:" + locale.getCode() + " Name:" + locale.getLabel());
	}

    }

    public static void callgetSFQueues() throws Exception {
	SFConnectionConfig sfConnectionConfig = getLoginDetailsFromMiddleware();
	SFKnowledgeArticleService sfKnowledgeArticleImpl = new SFKnowledgeArticleServiceImpl(sfConnectionConfig);
	List<SFQueue> sfqueue = sfKnowledgeArticleImpl.getSFQueues();
	for (SFQueue sfArticle : sfqueue) {
	    System.out.println("Id:" + sfArticle.getQueueId() + " Name:" + sfArticle.getQueueName());
	}

    }

    public static void callgetReadyArticleIdsForTranslation() throws Exception {
	SFConnectionConfig sfConnectionConfig = getLoginDetailsFromMiddleware();
	SFKnowledgeArticleService sfKnowledgeArticleImpl = new SFKnowledgeArticleServiceImpl(sfConnectionConfig);
	List<SFArticle> ReadyArticleIdsForTranslation = sfKnowledgeArticleImpl.getReadyArticleIdsForTranslation("fr", "offer__kav", "00G28000001Ep0TEAS", false);
	if (ReadyArticleIdsForTranslation.size() == 0) {
	    System.out.println("No records to display");
	}
	for (SFArticle sfArticle : ReadyArticleIdsForTranslation) {
	    System.out.println("ArticleId:" + sfArticle.getId() + "==========MasterVersion Id:" + sfArticle.getMasterVersionId() + "=======language:" + sfArticle.getLanguage() + "=====Type:" + sfArticle.getType());

	}

    }

    public static void callgetArticleStreamForTranslation() throws Exception {
	SFConnectionConfig sfConnectionConfig = getLoginDetailsFromMiddleware();
	SFKnowledgeArticleService sfKnowledgeArticleImpl = new SFKnowledgeArticleServiceImpl(sfConnectionConfig);
	List<SFArticle> ReadyArticleIdsForTranslation = sfKnowledgeArticleImpl.getReadyArticleIdsForTranslation("fr", "offer__kav", "00G28000001Ep0TEAS", true);
	SFArticle sfArticle = new SFArticle();
	for (SFArticle sfArticleObj : ReadyArticleIdsForTranslation) {
	    System.out.println(sfArticleObj.getId() + "==========MasterVersion Id:" + sfArticleObj.getMasterVersionId());
	    sfArticle = sfArticleObj;
	}

	sfArticle.setType("offer__kav");
	sfArticle.setLanguage("fr");

	List<SFArticleField> fieldsInfo = getSFArticleCustomFieldList();
	InputStream ArticleStreamTransaltion = sfKnowledgeArticleImpl.getArticleStreamForTranslation(sfArticle, fieldsInfo);
	BufferedReader br = new BufferedReader(new InputStreamReader(ArticleStreamTransaltion));
	String read;

	while ((read = br.readLine()) != null) {
	    System.out.println(read);
	}

    }

    public static void callimportTranslatedArticle() throws Exception {
	SFConnectionConfig sfConnectionConfig = getLoginDetailsFromMiddleware();
	SFKnowledgeArticleService sfKnowledgeArticleImpl = new SFKnowledgeArticleServiceImpl(sfConnectionConfig);
	SFArticle sfArticle = new SFArticle();
	sfArticle.setId("ka028000000I5JZAA0");
	sfArticle.setType("offer");
	InputStream inputstream = new FileInputStream("E://input1.txt");
	sfKnowledgeArticleImpl.importTranslatedArticle(sfArticle, null, inputstream);
    }
}
