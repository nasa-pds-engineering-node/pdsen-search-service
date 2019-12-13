import os
import requests
from lxml import html

# -------------------------------------------------------------------------

def download_file(url, destFile):
    page = requests.get(url)
    f = open(destFile, 'w')
    f.write(page.text)
    f.close()

# -------------------------------------------------------------------------

def get_file_refs(url):
    page = requests.get(url)
    tree = html.fromstring(page.content)

    hrefs = tree.xpath('//tr/td/a')

    # There are duplicate hrefs. Extract unique values.
    files = set()
    for href in hrefs:
        str = href.attrib['href']
        if str.endswith('.xml'):
            files.add(str)
    
    return files

# -------------------------------------------------------------------------

def get_product_url(product, ver):
    if ver == 'pds4':
        baseUrl = 'https://starbase.jpl.nasa.gov/pds4/context-pds4/'
        url = baseUrl + product + '/'
    elif ver == 'pds3':
        baseUrl = 'https://starbase.jpl.nasa.gov/pds4/context-pds3/'
        url = baseUrl + product + '/Product/'
    else:
        raise Exception('Invalid PDS version: {}'.format(ver))
    return url

# -------------------------------------------------------------------------

def get_dest_dir(product, ver):
    baseDestDir = '/d2/PDS/context/'
    destDir = baseDestDir + ver + '/' + product + '/'
    
    if not os.path.exists(destDir):
        os.makedirs(destDir)
    
    return destDir

# -------------------------------------------------------------------------

def main():
    product = 'airborne'
    pdsVer = 'pds4'

    productUrl = get_product_url(product, pdsVer)
    destDir = get_dest_dir(product, pdsVer)

    # Parse HTML page and extract file names from hrefs
    files = get_file_refs(productUrl)
    print("Found {} files".format(len(files)))

    # Copy each file
    for fname in files:
        fileUrl = productUrl + fname
        destFile = destDir + fname
        download_file(fileUrl, destFile)

# -------------------------------------------------------------------------

if __name__ == '__main__':
    main()

