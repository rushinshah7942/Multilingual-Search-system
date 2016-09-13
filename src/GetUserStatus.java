
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.rmtheis.yandtran.language.Language;
import com.rmtheis.yandtran.translate.Translate;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import twitter4j.HashtagEntity;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TweetEntity;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;
import twitter4j.auth.AccessToken;

public class GetUserStatus {

	public static void main(String[] args) {

		//Query englishQuery = new Query("(Ankara OR daesh OR ISIL OR syrianCrisis OR refugee OR Bashar al-Assad OR ISIS OR Putin OR Turkey OR Russia OR Syria) and lang:en");
		Query englishQuery = new Query("#PrayForSyria");
		englishQuery.setLang("en");
		englishQuery.setSince("2015-11-01");
		englishQuery.setUntil("2015-12-11");
		Query germanQuery= new Query("Ankara OR Merkel OR Deutschland OR daesh OR ISIL OR syrische Krise OR ISIS OR refugee OR Bashar al-Assad OR Flüchtling OR Turkey OR Russland OR Syria");
		germanQuery.setLang("de");
		Query arabicQuery= new Query("(أنقرة OR الأزمة السورية OR ISIS OR اللائذ OR Turkey OR بشار الأسد  OR روسيا OR سوريا OR putin )");
		arabicQuery.setLang("ar");
		//(Ankara OR أنقرة OR daesh OR ISIL OR الأزمة السورية OR ISIS OR refugee OR اللائذ OR Turkey OR Bashar al-Assad OR بشار الأسد OR Russia OR روسيا OR Putin OR Syria OR سوريا )
		Query russianQuery= new Query("Анкара OR ISIL OR ISIS OR Россия OR Путин OR Турция OR Сирия OR Башар аль-Асад OR Putin OR Москва OR беженец OR сирийского кризиса OR беглец OR Подробности");
		russianQuery.setLang("ru");
		Query frenchQuery= new Query("Ankara OR François Hollande OR Paris OR daesh OR Bashar al-Assad OR ISIL OR crise syrienne OR ISIS OR réfugié OR Turkey OR Russie OR Syria");
		frenchQuery.setLang("fr");

		List<Query> queryList=new ArrayList<>();
		queryList.add(englishQuery);
		//queryList.add(russianQuery);
		//queryList.add(germanQuery);
		//queryList.add(arabicQuery);
		//queryList.add(frenchQuery);



		Twitter twitter= new TwitterFactory().getInstance();
		twitter.setOAuthConsumer("2auhiiDGj3V8j0U7gotdjzM75","olL59ab0bGcekV8FeahdHI0308CXkVfRNThcpKeeSdsKWnm3X9");
		twitter.setOAuthAccessToken(new AccessToken("3523962137-u1cV7n6QmM0VXw16J6rHF2AgPt6y2Y7L6jtpUah","xfQqireAobRH7fL11Mf9WpgZDSZVtscDc9ZX9Zy0iCqWb"));

		try{
			QueryResult result;
			int count=0;
			for(Query query : queryList){	
				int totalRound=0;

				count++;
				List<UserEntity> userEntitiesList= new ArrayList<>();
				while(totalRound<4){

					int miniRound=0;
					while(miniRound<10){
						
						query.setCount(100);
						result=twitter.search(query);
						//query=result.nextQuery();
						//result=twitter.search(englishQuery);

						//System.out.println("Twitter Counts for lang " + count + " : " + result.getCount());

						List<Status> statusList = result.getTweets();
						System.out.println("no. of tweets retrieved"+statusList.size());
						System.out.println("no of minirounds: "+miniRound);
						for (Status tweet : statusList) {					

							UserEntity userEntity=new UserEntity();

							if(count==1){
								userEntity.setText_en(tweet.getText());
							}
							else if(count==2){
								userEntity.setText_ru(tweet.getText());
							}
							else if(count==3){
								userEntity.setText_de(tweet.getText());
							}
							else if(count==4){
								userEntity.setText_ar(tweet.getText());
							}
							else if(count==5){
								userEntity.setText_fr(tweet.getText());
							}

							userEntity.setCreated_at(GetUserStatus.tweeterToSolrDateConverter(tweet.getCreatedAt()));					
							userEntity.setTweetSource(tweet.getSource());
							userEntity.setUserId(tweet.getUser().getId());
							userEntity.setFavorite_count(tweet.getFavoriteCount());
							userEntity.setRetweet_count(tweet.getRetweetCount());
							userEntity.setLang(tweet.getLang());
							userEntity.setUserLocation(tweet.getUser().getLocation());
							userEntity.setUserName(tweet.getUser().getName());
							userEntity.setUserScreenName(tweet.getUser().getScreenName());
							userEntity.setUserTimeZone(tweet.getUser().getTimeZone());
							userEntity.setMentionedURLInTweet(tweet.getUser().getURLEntity().getURL());
							userEntity.setUrl("https://twitter.com/" + tweet.getUser().getScreenName() + "/status/" + tweet.getId());
							userEntity.setUserDescription(tweet.getUser().getDescription());
							//userEntity.setTweet_urls(tweet.getUser().getURLEntity().getExpandedURL());
							//tweet.getUserMentionEntities();

							List<String> userMentionedList= new ArrayList<>();
							for(UserMentionEntity userMentionEntity : tweet.getUserMentionEntities()){
								userMentionedList.add(userMentionEntity.getScreenName());
							}
							userEntity.setUserMentioned(userMentionedList);

							List<String> urlEntitiesList= new ArrayList<>();
							for(URLEntity urlEntity : tweet.getURLEntities()){
								urlEntitiesList.add(urlEntity.getExpandedURL());
							}
							userEntity.setTweet_urls(urlEntitiesList);

							List<String> hashtagsText = new ArrayList<>();
							for(HashtagEntity hashtagEntity : tweet.getHashtagEntities()){
								hashtagsText.add(hashtagEntity.getText());
							}

							//System.out.println(hashtagsText);
							userEntity.setTweet_hashtags(hashtagsText);

							userEntitiesList.add(GetUserStatus.stanfordTagger(userEntity));

						}
						miniRound++;
					}
					Gson gson= new Gson();
					String userEntityJSON = gson.toJson(userEntitiesList);
					//System.out.println("UserEntity : "+ userEntityJSON);
					//System.out.println("UserEntity : "+ userEntityJSON);
					if(count==1){
						FileWriter fileWriter=new FileWriter("E:/Fall-15/CSE 535 Information Retrieval/Project/FinalProject_PartC/Data/Twitter data English/TwitterDataFileEnglishDay3_round"+totalRound+"_projc.json", true);
						fileWriter.write(userEntityJSON);
						fileWriter.close();
						System.out.println("UserEntity : "+ userEntityJSON);
						userEntityJSON=null;
						userEntitiesList.clear();
						System.out.println("Successfully entered English data");
					}
					else if (count==2) {
						FileWriter fileWriter=new FileWriter("E:/Fall-15/CSE 535 Information Retrieval/Project/FinalProject_PartC/Data/Twitter Data Russian/TwitterDataFileRussianDay2_round"+totalRound+"_projc.json", true);
						fileWriter.write(userEntityJSON);
						fileWriter.close();
						System.out.println("UserEntity : "+ userEntityJSON);
						userEntityJSON=null;
						userEntitiesList.clear();
						System.out.println("successfully entered Russian data");
					}
					else if (count==3){
						FileWriter fileWriter=new FileWriter("E:/Fall-15/CSE 535 Information Retrieval/Project/FinalProject_PartC/Data/Twitter Data German/TwitterDataFileGermanDay2_round"+totalRound+"_projc.json", true);
						fileWriter.write(userEntityJSON);
						fileWriter.close();
						System.out.println("UserEntity : "+ userEntityJSON);
						userEntityJSON=null;
						userEntitiesList.clear();
						System.out.println("successfully entered German data");
					}
					else if (count==4){
						FileWriter fileWriter=new FileWriter("E:/Fall-15/CSE 535 Information Retrieval/Project/FinalProject_PartC/Data/Twitter Data Arabic/TwitterDataFileArabicDay2_round"+totalRound+"_projc.json", true);
						fileWriter.write(userEntityJSON);
						fileWriter.close();
						System.out.println("UserEntity : "+ userEntityJSON);
						userEntityJSON=null;
						userEntitiesList.clear();
						System.out.println("successfully entered Arabic data");
					}
					else if (count==5){
						FileWriter fileWriter=new FileWriter("E:/Fall-15/CSE 535 Information Retrieval/Project/FinalProject_PartC/Data/Twitter Data French/TwitterDataFileFrenchDay2_round"+totalRound+"_projc.json", true);
						fileWriter.write(userEntityJSON);
						fileWriter.close();
						System.out.println("UserEntity : "+ userEntityJSON);
						userEntityJSON=null;
						userEntitiesList.clear();
						System.out.println("successfully entered French data");
					}
					totalRound++;
				}
			}
			System.exit(0);
		}catch(Exception te) {			
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			System.exit(-1);
		}
	}

	public static String tweeterToSolrDateConverter(Date tweeterDate){
		DateFormat df = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss'Z'");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		return df.format(tweeterDate);

	}

	public static UserEntity stanfordTagger(UserEntity userEntity) throws Exception {

		Translate.setKey("trnsl.1.1.20151208T214555Z.b8f9acaab84ad44d.8310d18af232434575253d7d2ace8b3a69cb8158");

		

		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, regexner");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		String text="";

		if(userEntity.getText_en()!=null){
			text = userEntity.getText_en(); // Add your text here!
		}
		else if(userEntity.getText_ru()!=null){
			text = Translate.execute(userEntity.getText_ru(), Language.RUSSIAN,Language.ENGLISH); // Add your text here!

		}
		else if(userEntity.getText_de()!=null){
			text = Translate.execute(userEntity.getText_de(), Language.GERMAN,Language.ENGLISH);// Add your text here!
		}
		else if(userEntity.getText_ar()!=null){
			text = userEntity.getText_ar(); // Add your text here!
		}
		else if(userEntity.getText_fr()!=null){
			text = Translate.execute(userEntity.getText_fr(), Language.FRENCH,Language.ENGLISH); // Add your text here!
		}
		String newText=new String();
		newText=text;
		for(String hashtags:userEntity.getTweet_hashtags()){
			newText=newText+","+hashtags;
		}
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(newText);

		// run all Annotators on this text
		pipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		List<String> organizations=new ArrayList<String>();
		List<String> person=new ArrayList<String>();
		List<String> location=new ArrayList<String>();
		for(CoreMap sentence: sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods

			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {

				// this is the text of the token
				String word = token.get(TextAnnotation.class);
				// this is the POS tag of the token
				//String pos = token.get(PartOfSpeechAnnotation.class);
				// this is the NER label of the token
				String ne = token.get(NamedEntityTagAnnotation.class);  
				if(ne.equals("ORGANIZATION")){
					if(!organizations.contains(word)){
						organizations.add(word);
					}
				}
				else if(ne.equals("LOCATION")){
					if(!location.contains(word)){
						location.add(word);
					}
				}
				else if(ne.equals("PERSON")){
					if(!person.contains(word)){
						person.add(word);
					}
				}
				// this is the sentiment label of the token
				//String sentiment = token.get(SentimentCoreAnnotations.SentimentClass.class);
				//this is the relationship label of the token MachineReadingAnnotations
				//String relation = token.get(MachineReadingAnnotations.RelationMentionsAnnotation.class).toString();

				//System.out.println("word: "+word+", pos: "+pos+", ne: "+ne+", token:"+token.toString());
			}

			// this is the parse tree of the current sentence
			//Tree tree = sentence.get(SentimentCoreAnnotations.class);
			//System.out.println("tree: "+tree.toString());
			// this is the Stanford dependency graph of the current sentence
			//SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
			//System.out.println("dependencies: "+dependencies.toString());
		}
		if(text==null){
			System.out.println("text is null");
		}
		int sentiment=GetUserStatus.findSentiment(text);
		//System.out.println("sentiment: "+sentiment);
		userEntity.setSentiment(sentiment);
		userEntity.setOrganizationTag(organizations);
		userEntity.setLocationTag(location);
		userEntity.setPersonTag(person);
		// This is the coreference link graph
		// Each chain stores a set of mentions that link to each other,
		// along with a method for getting the most representative mention
		// Both sentence and token offsets start at 1!
		// Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
		//System.out.println("userEntity:"+userEntity.toString());
		return userEntity;
	}

	public static int findSentiment(String tweet) {

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		int mainSentiment = 0;
		if (tweet != null && tweet.length() > 0) {
			int longest = 0;
			Annotation annotation = pipeline.process(tweet);
			for (CoreMap sentence : annotation
					.get(CoreAnnotations.SentencesAnnotation.class)) {
				Tree tree = sentence
						.get(SentimentAnnotatedTree.class);
				int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
				String partText = sentence.toString();
				if (partText.length() > longest) {
					mainSentiment = sentiment;
					longest = partText.length();
				}

			}
		}
		return mainSentiment;
	}




}
