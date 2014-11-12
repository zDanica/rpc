package com.linda.framework.rpc.server;

import com.linda.framework.rpc.filter.RpcFilter;
import com.linda.framework.rpc.net.AbstractRpcAcceptor;
import com.linda.framework.rpc.net.AbstractRpcNetworkBase;
import com.linda.framework.rpc.nio.ConcurrentRpcNioSelector;
import com.linda.framework.rpc.nio.RpcNioAcceptor;
import com.linda.framework.rpc.nio.SimpleRpcNioSelector;

public class RpcServer extends AbstractRpcNetworkBase{

	private AbstractRpcAcceptor acceptor;
	private RpcServiceProvider provider = new RpcServiceProvider();
	private SimpleServerRemoteExecutor proxy = new SimpleServerRemoteExecutor();
	
	public void setAcceptor(AbstractRpcAcceptor acceptor){
		this.acceptor = acceptor;
	}
	
	public void addRpcFilter(RpcFilter filter){
		provider.addRpcFilter(filter);
	}
	
	public <Iface> void register(Class<Iface> clazz,Iface ifaceImpl){
		proxy.registerRemote(clazz, ifaceImpl);
	}
	
	@Override
	public void setHost(String host) {
		checkAcceptor();
		super.setHost(host);
		acceptor.setHost(host);
	}

	@Override
	public void setPort(int port) {
		checkAcceptor();
		super.setPort(port);
		acceptor.setPort(port);
	}

	@Override
	public void startService() {
		provider.setExecutor(proxy);
		acceptor.addRpcCallListener(provider);
		acceptor.startService();
	}

	@Override
	public void stopService() {
		acceptor.stopService();
		proxy.stopService();
		provider.stopService();
	}
	
	private void checkAcceptor(){
		if(acceptor==null){
			ConcurrentRpcNioSelector selector = new ConcurrentRpcNioSelector();
			acceptor = new RpcNioAcceptor(selector);
		}
	}

}
