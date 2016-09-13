# -*- coding: utf8 -*-

from __future__ import absolute_import
from __future__ import division, print_function, unicode_literals

from sumy.parsers.html import HtmlParser
from sumy.parsers.plaintext import PlaintextParser
from sumy.nlp.tokenizers import Tokenizer
from sumy.summarizers.luhn import LuhnSummarizer as Summarizer
from sumy.nlp.stemmers import Stemmer
from sumy.models.dom import Sentence
from sumy.utils import get_stop_words
from mstranslator import Translator
import json
import codecs
LANGUAGE = "english"
SENTENCES_COUNT = 10


if __name__ == "__main__":

	#url = "https://en.wikipedia.org/wiki/Barack_Obama"
	#parser = HtmlParser.from_url(url, Tokenizer(LANGUAGE))
	# or for plain text files
	#inurl = 'http://ruhan.koding.io:8983/solr/partb/select?q=*%3A*&fl=id%2Cscore&wt=json&indent=true&rows=1000'
	#outfn = 'path to your file.txt'
	#data = urllib2.urlopen(inurl)

	with open('newinput.json','r') as data_file:    
		data = json.load(data_file)
	#print(data)
	tweet_hashtags = []
	hashtag_summary_dictionary = {}

	for dataTemp in data:
		if 'tweet_hashtags' in dataTemp:
			for hashtags in dataTemp['tweet_hashtags']:
				#print(hashtags.encode('utf8'))
				tweet_hashtags.append(hashtags.encode('utf8'))

	tweet_hashtags = list(set(tweet_hashtags))		
	# print(tweet_hashtags)

	for hashtags in tweet_hashtags:
		hashtag_summary_dictionary[hashtags] = ''
	
	for dataTemp in data:
		lang = dataTemp['lang']
		if 'tweet_hashtags' in dataTemp:
			for hashtag in dataTemp['tweet_hashtags']:
				if hashtag in hashtag_summary_dictionary:
					str = hashtag_summary_dictionary[hashtag]
					if 'text_en' in dataTemp:
						strTemp = dataTemp['text_en'][0].encode('ascii','ignore').decode('unicode_escape')
						str = strTemp+ '.'+str
						str.encode('utf-8')
						# print(str)
						hashtag_summary_dictionary[hashtag] = str
	
	# print(hashtag_summary_dictionary['Canada'].encode('utf8'))

	#print(hashtag_summary_dictionary)		
	#print(hashtag_summary_dictionary)	
	#t.set_credentials(app_id='tejash125',client_id='tejash125',client_secret='lZoX1UJLRmoHlL0qX1i1FVDGtNkeS5J09odjkyqbbY8')
	#translator = Translator('tejash125', 'lZoX1UJLRmoHlL0qX1i1FVDGtNkeS5J09odjkyqbbY8')
	
	# print(translator.translate('Привет, мир!', lang_from='ru', lang_to='en'))
	
	summarized = []
	for key,values in hashtag_summary_dictionary.items():
		# print(hashtag_summary_dictionary)
		# print(key.encode('ascii','ignore').decode('unicode_escape') +':'+hashtag_summary_dictionary[key].encode('ascii','ignore').decode('unicode_escape'))
		f = open('myfile.txt','w')
		#print(key)
		f.write(values) # python will convert \n to os.linesep
		f.close()
		parser = PlaintextParser.from_file("myfile.txt", Tokenizer(LANGUAGE))
		stemmer = Stemmer(LANGUAGE)
		summarizer = Summarizer(stemmer)
		summarizer.stop_words = get_stop_words(LANGUAGE)
		summary = ''
		final_summary = {}

		for sentence in summarizer(parser.document, SENTENCES_COUNT):
			#summary += summary + str(sentence)
			#summary.append(sentence)
			
			with open('Output.txt','w') as text_file:
				print(sentence,file=text_file)
				# print('-----------------------',file=text_file)

    		with open('Output.txt','r') as data_file:
    			summary += summary + data_file.read(); 
    			final_summary['hashtag'] = key
    			final_summary['summary'] = summary
    			summarized.append(final_summary)
    			

for dictionary in summarized:
	with open('newsummary.txt','a') as text_file:
		print(dictionary,file = text_file)    
				
