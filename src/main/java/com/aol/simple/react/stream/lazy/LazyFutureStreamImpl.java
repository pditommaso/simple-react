package com.aol.simple.react.stream.lazy;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.Wither;
import lombok.extern.slf4j.Slf4j;

import com.aol.simple.react.async.factories.QueueFactories;
import com.aol.simple.react.async.factories.QueueFactory;
import com.aol.simple.react.async.subscription.Continueable;
import com.aol.simple.react.async.subscription.Subscription;
import com.aol.simple.react.capacity.monitor.LimitingMonitor;
import com.aol.simple.react.collectors.lazy.BatchingCollector;
import com.aol.simple.react.collectors.lazy.LazyResultConsumer;
import com.aol.simple.react.stream.BaseSimpleReact;
import com.aol.simple.react.stream.StreamWrapper;
import com.aol.simple.react.stream.traits.LazyFutureStream;
import com.aol.simple.react.threads.ReactPool;
import com.nurkiewicz.asyncretry.RetryExecutor;

@Wither
@Builder
@Getter
@Slf4j 
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class LazyFutureStreamImpl<U> implements LazyFutureStream<U>{
	
	


	private final Optional<Consumer<Throwable>> errorHandler;
	private final StreamWrapper lastActive;
	private final boolean eager;
	private final Consumer<CompletableFuture> waitStrategy;
	private final LazyResultConsumer<U> lazyCollector;
	private final QueueFactory<U> queueFactory;
	private final LazyReact simpleReact;
	private final Continueable subscription;
	private final static ReactPool<BaseSimpleReact> pool = ReactPool.elasticPool(()->new LazyReact(Executors.newSingleThreadExecutor()));
	private final List originalFutures=  null;
	private final ParallelReductionConfig parallelReduction;
	private final ConsumerHolder error;
	private final ExecutorService publisherExecutor = Executors.newFixedThreadPool(1);
	@AllArgsConstructor
	static class ConsumerHolder{
		volatile Consumer<Throwable> forward;
	}
	
	
	public LazyFutureStreamImpl(LazyReact lazyReact, final Stream<CompletableFuture<U>> stream) {
		
		this.simpleReact = lazyReact;
		Stream s = stream;
		this.lastActive = new StreamWrapper(s, false);
		this.error =  new ConsumerHolder(a->{});
		this.errorHandler = Optional.of((e) -> { error.forward.accept(e); log.error(e.getMessage(), e);});
		this.eager = false;
		this.waitStrategy = new LimitingMonitor(lazyReact.getMaxActive());
		this.lazyCollector = new BatchingCollector<>(this);
		this.queueFactory = QueueFactories.unboundedNonBlockingQueue();
		this.subscription = new Subscription();
		this.parallelReduction = ParallelReductionConfig.defaultValue;
		
		
		
	}
	
	public void forwardErrors(Consumer<Throwable> c){
		error.forward =c;
	}

	
	public BaseSimpleReact getPopulator(){
		return pool.nextReactor();
	}
	public void returnPopulator(BaseSimpleReact service){
		pool.populate(service);
	}
	
	@Override
	public <R, A> R collect(Collector<? super U, A, R> collector) {
		return block(collector);
	}



	@Override
	public LazyFutureStream<U> withAsync(boolean b) {
		
		return this.withSimpleReact(this.simpleReact.withAsync(b));
	}



	@Override
	public Executor getTaskExecutor() {
		return this.simpleReact.getExecutor();
	}



	@Override
	public RetryExecutor getRetrier() {
		return this.simpleReact.getRetrier();
	}



	@Override
	public boolean isAsync() {
		return this.simpleReact.isAsync();
	}



	@Override
	public LazyFutureStream<U> withTaskExecutor(Executor e) {
		return this.withSimpleReact(simpleReact.withExecutor(e));
	}



	@Override
	public LazyFutureStream<U> withRetrier(RetryExecutor retry) {
		return this.withSimpleReact(simpleReact.withRetrier(retry));
	}
	
  
	
}
