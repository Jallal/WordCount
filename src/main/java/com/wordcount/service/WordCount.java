package com.wordcount.service;

import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class WordCount {
	private static final Pattern WORD_BOUNDARY = Pattern.compile("\\s*\\b\\s*");
	private final static IntWritable one = new IntWritable(1);
	private final static IntWritable two = new IntWritable(1);

	public void CountWordsInFile(String[] args) throws Exception {
		Configuration c = new Configuration();
		String[] files = new GenericOptionsParser(c, args).getRemainingArgs();
		Path input = new Path(files[0]);
		Path output = new Path(files[1]);
		Job j = new Job(c, "wordcount");
		j.setWorkingDirectory(input.getParent());
		j.setJarByClass(WordCount.class);
		j.setMapperClass(MapForWordCount.class);
		j.setReducerClass(ReduceForWordCount.class);
		j.setOutputKeyClass(Text.class);
		j.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(j, input);
		FileOutputFormat.setOutputPath(j, output);
		System.exit(j.waitForCompletion(true) ? 0 : 1);
	}

	public static class MapForWordCount extends Mapper<LongWritable, Text, Text, IntWritable> {
		public void map(LongWritable key, Text value, Context con) throws IOException, InterruptedException {
			String line = value.toString();
			Text currentWord = new Text();

			for (String word : WORD_BOUNDARY.split(line)) {
				if (word.isEmpty()) {
					continue;
				}
				currentWord = new Text(word);
				if ((currentWord.getLength() > 1) && (!Character.isDigit(currentWord.charAt(0)))
						&& (Character.isLetter(currentWord.charAt(0)))) {
					con.write(currentWord, one);
				} else {
					if (Character.isLetter(currentWord.charAt(0)) || Character.isDigit(currentWord.charAt(0))
							|| (!Pattern.matches("\\p{Punct}", currentWord.toString()))) {
						// don't add it will be counted with the letters
					} else {
						con.write(currentWord, one);
					}
				}
				for (int i = 0; i < word.length(); i++) {
					char c = word.charAt(i);
					if (Character.isLetter(c)) {
						char newC = Character.toUpperCase(c);
						Text currenChar = new Text(String.valueOf(newC));
						con.write(currenChar, two);
					}
				}
			}
		}
	}

	public static class ReduceForWordCount extends Reducer<Text, IntWritable, Text, IntWritable> {
		@Override
		public void reduce(Text word, Iterable<IntWritable> counts, Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable count : counts) {
				sum += count.get();
			}
			context.write(word, new IntWritable(sum));
		}
	}
}
