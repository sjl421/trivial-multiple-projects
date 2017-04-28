[1]:http://hangyudu.oss-cn-shanghai.aliyuncs.com/04_java_performance/traditional%20software%20development%20process.png


Strategies, Approaches, and Methodologies
=========================================

## Forces at Play

It is generally accepted at a high level that the traditional software development
process consists of four major phases: `analysis`, `design`, `coding`, and `testing`. How these
phases flow to together is illustrated in Figure 1-1.

![tradition software development process][1]

- What is the expected throughput of the application?
- What is the expected latency between a stimulus and a response to that stimulus?
- How many concurrent users or concurrent tasks shall the application support?
- What is the accepted throughput and latency at the maximum number of concurrent users or concurrent tasks?
- What is the maximum worst case latency?
- What is the frequency of garbage collection induced latencies that will be tolerated?
