# Spark ML Decision Tree Example
Extending the Spark ML examples to a real world text dataset. You never see these and this is a exercise to share the concept on real text data.

## Instructions:
Clone the repo to your IDE. <br>
Create your Google Cloud Dataproc cluster <br>
Create a storage bucket. <br>
Grab the datafile from UCI ML repo <br>
Upload datfile to storage bucket <br>
Edit the scala file on line 42 with the location of the datafile <br>
From the base directory run: <br>
sbt clean assembly<br>
Upload the jar to the Storage Bucket <br>
Launch the job pointing the the uploaded jar.<br>





This example runs on the UCI Machine Learning repo dataset found here: <br>
https://archive.ics.uci.edu/ml/datasets/SMS+Spam+Collection

<br>

Be careful to clean non-printing characters from the dataset first. <br>

