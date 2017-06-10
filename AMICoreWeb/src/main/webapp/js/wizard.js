/*-------------------------------------------------------------------------*/

var charArray = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890';

/*-------------------------------------------------------------------------*/

function generateEncryptionKey()
{
	var encryptionKey = '';

	for(var i = 0; i < 16; i++)
	{
		encryptionKey += charArray.charAt(
			Math.floor(charArray.length * Math.random())
		);
	}

	$('#encryption_key').val(encryptionKey);
}

/*-------------------------------------------------------------------------*/

function validateForm()
{
	/*-----------------------------------------------------------------*/

	var error = '';

	var reset = false;

	$.each($(':input').serializeArray(), function(index, param) {

		if(param.name !== 'router_reset')
		{
			if(param.value === '')
			{
				error += ' Error, field `' + param.name + '` empty!';
			}
		}
		else
		{
			reset = true;
		}
	});

	/*-----------------------------------------------------------------*/

	if(error)
	{
		error = '<div class="alert alert-danger alert-dismissible">\n'
			+
		        '  <button class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>\n'
			+
		        '  <strong>Error!</strong>' + error + '\n'
			+
		        '</div>'
		;

		$('#message').html(error);

		$(document).scrollTop(0);

		return false;
	}

	/*-----------------------------------------------------------------*/

	if(reset && confirm('Reset the AMI database?') === false)
	{
		return false;
	}

	/*-----------------------------------------------------------------*/

	$('button[type="submit"]').prop('disabled', true);

	/*-----------------------------------------------------------------*/

	return true;
}

/*-------------------------------------------------------------------------*/
