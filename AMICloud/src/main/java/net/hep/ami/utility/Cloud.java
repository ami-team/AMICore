package net.hep.ami.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.net.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.domain.Ingress;
import org.jclouds.openstack.nova.v2_0.domain.Network;
import org.jclouds.openstack.nova.v2_0.domain.RebootType;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule.Cidr;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.domain.TenantIdAndName;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupApi;
import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.features.ImageApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.nova.v2_0.options.RebuildServerOptions;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Module;



public class Cloud {


	public static class CloudServer {

		public String region;
		public String serverID;
		public String serverLabel;

		public CloudServer(String _region, String _serverID, String _serverLabel){
			region = _region;
			serverID= _serverID;
		}

		public String toString() {
			return region + "," + serverID + "," + serverLabel;
		}
	}

	public static class CloudFlavor {

		public String region;
		public String flavorID;
		public String flavorLabel;

		public CloudFlavor(String _region, String _flavorID, String _flavorLabel){
			region = _region;
			flavorID = _flavorID;
			flavorLabel = _flavorLabel;
		}

		public String toString() {
			return region + "," + flavorID + "," + flavorLabel;
		}
	}

	public static class CloudImage {

		public String region;
		public String imageID;
		public String imageLabel;

		public CloudImage(String _region, String _imageID, String _imageLabel){
			region = _region;
			imageID = _imageID;
			imageLabel = _imageLabel;
		}

		public String toString() {
			return region + "," + imageID + "," + imageLabel;
		}
	}


	/*---------------------------------------------------------------------*/

	/* TODO */

	/*---------------------------------------------------------------------*/

	private  NovaApi novaApi;
	private  Set<String> m_regions;
	private  String m_networUUID;


	public Cloud(String _identity, String _endpoint, String _credential, String _networkUuid)
	{
		novaApi = ContextBuilder.newBuilder("openstack-nova")
				.endpoint(_endpoint)
				.credentials(_identity, _credential).buildApi(NovaApi.class);

		m_regions = novaApi.getConfiguredRegions();	
		m_networUUID = _networkUuid;
	}


	public Set<String> listRegions()
	{
		return m_regions;
	}

	public List<CloudServer> listServers()
	{
		List<CloudServer> result = new ArrayList<CloudServer>();

		for (String region : m_regions)
		{

			ServerApi serverApi = novaApi.getServerApi(region);

			for (Server server : serverApi.listInDetail().concat())
			{
				result.add(new CloudServer(
						region,
						server.getId(),
						server.getKeyName()
						));
			}
		}

		return result;
	}

	public List<CloudFlavor> listFlavors()
	{
		ArrayList <CloudFlavor> result = new ArrayList <CloudFlavor>();

		for (String region : m_regions)
		{
			ArrayList<CloudFlavor> flavors = new ArrayList<CloudFlavor>();

			FlavorApi flavorApi = novaApi.getFlavorApi(region);

			for (Flavor flavor : flavorApi.listInDetail().concat())
			{
				result.add(new CloudFlavor(region,flavor.getId(),flavor.getName()));
			}

		}
		return result;
	}

	public List<CloudImage> listImages()
	{
		ArrayList <CloudImage> result = new ArrayList <CloudImage>();

		for (String region : m_regions)
		{
			ArrayList<CloudImage> images = new ArrayList<CloudImage>();

			ImageApi imageApi = novaApi.getImageApi(region);

			for (Image image : imageApi.listInDetail().concat())
			{
				result.add(new CloudImage(region,image.getId(),image.getName()));
			}
		}
		return result;
	}


	public String bootServer(String region, String name, String imageId, String flavorId,
			String keypair, String network, Iterable<String> secGroup, String userData,String fixedIp)
	{
		ServerApi serverApi = novaApi.getServerApi(region);

		CreateServerOptions options = CreateServerOptions.Builder
				.keyPairName(keypair).novaNetworks(ImmutableSet.of(
						Network.builder()
						.networkUuid(m_networUUID)
						.fixedIp(fixedIp)
						.build()));

		System.out.println(options.toString());
		ServerCreated ser = serverApi.create(name, imageId, flavorId, options);
		return ser.getId();
	}

	public boolean deleteServer(String region, String instanceId)
	{
		ServerApi serverApi = novaApi.getServerApi(region);
		return serverApi.delete(instanceId);
	}

	public void rebootServer(String region, String serverId, RebootType type)
	{
		ServerApi serverApi = novaApi.getServerApi(region);
		serverApi.reboot(serverId, type);
	}

	public String createImageFromServer(String region, String imageName, String serverId)
	{
		ServerApi serverApi = novaApi.getServerApi(region);
		return serverApi.createImageFromServer(imageName, serverId);
	}

	public void rebuildServer(String region, String serverId, String imageId)
	{
		ServerApi serverApi = novaApi.getServerApi(region);
		RebuildServerOptions options = RebuildServerOptions.Builder
				.withImage(imageId);
		serverApi.rebuild(serverId, options);
	}

	public void deleteImage(String region, String imageId)
	{
		ImageApi imageApi = novaApi.getImageApi(region);
		imageApi.delete(imageId);
	}

	public void close() throws IOException
	{
		Closeables.close(novaApi, true);
	}







}
